#!/usr/bin/env bash
set -euo pipefail

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

main() {
  require_env DEPLOY_HOST_ALIAS
  require_env DEPLOY_TARGET
  require_env SSH_PRIVATE_KEY
  require_env SSH_KNOWN_HOSTS
  require_env SSH_CONFIG

  local deploy_root releases_dir release_name release_stage release_path
  local current_tmp legacy_release history_limit

  history_limit="${DEPLOY_HISTORY_LIMIT:-5}"
  deploy_root="$(dirname "$DEPLOY_TARGET")"
  releases_dir="$deploy_root/releases"
  release_name="$(date -u +%Y%m%d-%H%M%S)"
  release_stage="$releases_dir/${release_name}.tmp"
  release_path="$releases_dir/$release_name"
  current_tmp="$deploy_root/.index.tmp"
  legacy_release="$releases_dir/${release_name}-migrated-live"

  prepare_ssh

  ssh "$DEPLOY_HOST_ALIAS" "mkdir -p '$releases_dir' && rm -rf '$release_stage' && mkdir -p '$release_stage'"
  rsync -az --delete --quiet docs/.vitepress/dist/ "$DEPLOY_HOST_ALIAS:$release_stage/"
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
    sh <<'EOF'
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
EOF
}

main "$@"
