#!/usr/bin/env bash
set -euo pipefail

log() {
  printf '[deploy-docs] %s\n' "$*"
}

require_env() {
  local name="$1"
  if [[ -z "${!name:-}" ]]; then
    echo "missing required environment variable: $name" >&2
    exit 1
  fi
}

prepare_ssh() {
  mkdir -p ~/.ssh
  printf '%s\n' "$SSH_PRIVATE_KEY" > ~/.ssh/id_ed25519
  chmod 600 ~/.ssh/id_ed25519
  printf '%s\n' "$SSH_KNOWN_HOSTS" > ~/.ssh/known_hosts
  printf '%s\n' "$SSH_CONFIG" > ~/.ssh/config
  chmod 600 ~/.ssh/config
}

show_local_artifact_summary() {
  local dist_dir="$1"
  local file_count total_size

  file_count="$(find "$dist_dir" -type f | wc -l | tr -d ' ')"
  total_size="$(du -sh "$dist_dir" | awk '{print $1}')"

  log "本地产物目录: $dist_dir"
  log "本地产物文件数: $file_count"
  log "本地产物体积: $total_size"
}

show_remote_target_state() {
  ssh "$DEPLOY_HOST_ALIAS" env DEPLOY_TARGET="$DEPLOY_TARGET" sh <<'EOF' | sed 's/^/[deploy-docs] /'
set -eu

if [ -L "$DEPLOY_TARGET" ]; then
  printf '当前线上入口: 软链接 -> %s\n' "$(readlink "$DEPLOY_TARGET")"
elif [ -d "$DEPLOY_TARGET" ]; then
  printf '当前线上入口: 实体目录（首次切换时会迁移为历史版本）\n'
elif [ -e "$DEPLOY_TARGET" ]; then
  printf '当前线上入口: 非目录文件（切换时会被移除）\n'
else
  printf '当前线上入口: 不存在（将执行首次发布）\n'
fi
EOF
}

show_change_preview() {
  local dist_dir="$1"
  local preview_output
  local -a added=()
  local -a updated=()
  local -a deleted=()

  if ! preview_output="$(
    rsync -az --delete --dry-run --itemize-changes --out-format='%i %n%L' \
      "$dist_dir/" "$DEPLOY_HOST_ALIAS:$DEPLOY_TARGET/" 2>&1
  )"; then
    printf '%s\n' "$preview_output" | sed 's/^/[deploy-docs] [preview] /'
    return 1
  fi

  while IFS= read -r line; do
    local code path

    [[ -z "$line" ]] && continue

    if [[ "$line" == *deleting* ]]; then
      path="${line#*deleting }"
      path="${path#"${path%%[![:space:]]*}"}"
      deleted+=("$path")
      continue
    fi

    code="${line%% *}"
    path="${line#* }"

    [[ "$path" == "." ]] && continue

    if [[ "$code" == *+++++++++ ]]; then
      added+=("$path")
    else
      updated+=("$path")
    fi
  done <<<"$preview_output"

  if (( ${#added[@]} == 0 && ${#updated[@]} == 0 && ${#deleted[@]} == 0 )); then
    log "变更预演: 当前产物与线上版本一致"
    return 0
  fi

  log "变更预演: 新增 ${#added[@]} 项，更新 ${#updated[@]} 项，删除 ${#deleted[@]} 项"

  if (( ${#added[@]} > 0 )); then
    log "新增文件:"
    printf '%s\n' "${added[@]}" | sed 's/^/[deploy-docs]   + /'
  fi

  if (( ${#updated[@]} > 0 )); then
    log "更新文件:"
    printf '%s\n' "${updated[@]}" | sed 's/^/[deploy-docs]   ~ /'
  fi

  if (( ${#deleted[@]} > 0 )); then
    log "删除文件:"
    printf '%s\n' "${deleted[@]}" | sed 's/^/[deploy-docs]   - /'
  fi
}

main() {
  require_env DEPLOY_HOST_ALIAS
  require_env DEPLOY_TARGET
  require_env SSH_PRIVATE_KEY
  require_env SSH_KNOWN_HOSTS
  require_env SSH_CONFIG

  local dist_dir deploy_root releases_dir release_name release_stage release_path
  local current_tmp legacy_release history_limit

  dist_dir="docs/.vitepress/dist"
  history_limit="${DEPLOY_HISTORY_LIMIT:-5}"
  deploy_root="$(dirname "$DEPLOY_TARGET")"
  releases_dir="$deploy_root/releases"
  release_name="$(date -u +%Y%m%d-%H%M%S)"
  release_stage="$releases_dir/${release_name}.tmp"
  release_path="$releases_dir/$release_name"
  current_tmp="$deploy_root/.index.tmp"
  legacy_release="$releases_dir/${release_name}-migrated-live"

  prepare_ssh

  show_local_artifact_summary "$dist_dir"
  show_remote_target_state
  show_change_preview "$dist_dir"

  log "开始上传发布目录: releases/$release_name"
  ssh "$DEPLOY_HOST_ALIAS" "mkdir -p '$releases_dir' && rm -rf '$release_stage' && mkdir -p '$release_stage'"
  rsync -az --delete --stats "$dist_dir/" "$DEPLOY_HOST_ALIAS:$release_stage/" | sed 's/^/[deploy-docs] [rsync] /'
  log "上传完成，开始切换线上版本"
  ssh "$DEPLOY_HOST_ALIAS" \
    env \
      CURRENT_TMP="$current_tmp" \
      DEPLOY_TARGET="$DEPLOY_TARGET" \
      HISTORY_LIMIT="$history_limit" \
      LEGACY_RELEASE="$legacy_release" \
      RELEASE_NAME="$release_name" \
      RELEASE_PATH="$release_path" \
      RELEASE_STAGE="$release_stage" \
      RELEASES_DIR="$releases_dir" \
    sh <<'EOF' | sed 's/^/[deploy-docs] [remote] /'
set -eu

chown -R 1000:1000 "$RELEASE_STAGE"
mv "$RELEASE_STAGE" "$RELEASE_PATH"

if [ -L "$DEPLOY_TARGET" ]; then
  :
elif [ -d "$DEPLOY_TARGET" ]; then
  mv "$DEPLOY_TARGET" "$LEGACY_RELEASE"
  chown -R 1000:1000 "$LEGACY_RELEASE"
elif [ -e "$DEPLOY_TARGET" ]; then
  rm -rf "$DEPLOY_TARGET"
fi

ln -s "releases/$RELEASE_NAME" "$CURRENT_TMP"
mv -Tf "$CURRENT_TMP" "$DEPLOY_TARGET"
chown -h 1000:1000 "$DEPLOY_TARGET"

find "$RELEASES_DIR" -mindepth 1 -maxdepth 1 -type d -printf '%f\n' \
  | sort -r \
  | awk -v limit="$HISTORY_LIMIT" 'NR > limit' \
  | while IFS= read -r old_release; do
      rm -rf "$RELEASES_DIR/$old_release"
    done

printf '已激活版本: %s\n' "$RELEASE_NAME"
printf '当前线上入口: %s\n' "$(readlink "$DEPLOY_TARGET")"
printf '保留的历史版本:\n'
find "$RELEASES_DIR" -mindepth 1 -maxdepth 1 -type d -printf '%f\n' \
  | sort -r \
  | head -n "$HISTORY_LIMIT" \
  | sed 's/^/ - /'
EOF

  log "部署完成"
}

main "$@"
