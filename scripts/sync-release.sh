#!/usr/bin/env bash
set -euo pipefail

log() {
  printf '[sync-release] %s\n' "$*"
}

fail() {
  printf '[sync-release] %s\n' "$*" >&2
  exit 1
}

require_env() {
  local name="$1"
  if [[ -z "${!name:-}" ]]; then
    fail "missing required environment variable: $name"
  fi
}

urlencode() {
  jq -rn --arg value "$1" '$value | @uri'
}

append_query_param() {
  local url="$1"
  local query="$2"

  if [[ "$url" == *\?* ]]; then
    printf '%s&%s' "$url" "$query"
  else
    printf '%s?%s' "$url" "$query"
  fi
}

absolute_url() {
  local base_url="$1"
  local value="$2"

  if [[ "$value" == http://* || "$value" == https://* ]]; then
    printf '%s' "$value"
  else
    printf '%s%s' "${base_url%/}" "$value"
  fi
}

expect_status() {
  local actual="$1"
  shift

  local expected
  for expected in "$@"; do
    if [[ "$actual" == "$expected" ]]; then
      return 0
    fi
  done

  fail "unexpected HTTP status: $actual, response: ${API_RESPONSE_BODY:-<empty>}"
}

parse_repo_slug() {
  local slug="$1"
  if [[ "$slug" != */* ]]; then
    fail "invalid repository slug: $slug"
  fi
}

api_request() {
  local method="$1"
  local url="$2"
  local auth_header="$3"
  shift 3

  local response_file http_code
  response_file="$(mktemp)"

  local -a curl_args=(
    --silent
    --show-error
    --location
    --request "$method"
    --output "$response_file"
    --write-out '%{http_code}'
  )

  if [[ -n "$auth_header" ]]; then
    curl_args+=(-H "Authorization: $auth_header")
  fi

  while (($#)); do
    curl_args+=("$1")
    shift
  done

  curl_args+=("$url")

  API_RESPONSE_STATUS="$(curl "${curl_args[@]}")"
  API_RESPONSE_BODY="$(cat "$response_file")"
  rm -f "$response_file"
}

github_api() {
  local method="$1"
  local path="$2"
  shift 2

  api_request \
    "$method" \
    "${GITHUB_API_URL%/}$path" \
    "Bearer $GITHUB_TOKEN" \
    -H "Accept: application/vnd.github+json" \
    -H "X-GitHub-Api-Version: 2022-11-28" \
    "$@"
}

codeberg_api() {
  local method="$1"
  local path="$2"
  shift 2

  api_request \
    "$method" \
    "${CODEBERG_API_BASE_URL%/}$path" \
    "token $CODEBERG_TOKEN" \
    -H "Accept: application/json" \
    "$@"
}

cnb_api() {
  local method="$1"
  local path="$2"
  shift 2

  api_request \
    "$method" \
    "${CNB_API_BASE_URL%/}$path" \
    "Bearer $CNB_TOKEN" \
    -H "Accept: application/vnd.cnb.api+json" \
    "$@"
}

source_has_asset_name() {
  local target_name="$1"
  local asset_name

  for asset_name in "${SOURCE_ASSET_NAMES[@]}"; do
    if [[ "$asset_name" == "$target_name" ]]; then
      return 0
    fi
  done

  return 1
}

download_source_release() {
  local source_path
  source_path="/repos/${SOURCE_OWNER_URI}/${SOURCE_REPO_NAME_URI}/releases/${SOURCE_RELEASE_ID}"

  github_api GET "$source_path"
  expect_status "$API_RESPONSE_STATUS" 200

  SOURCE_RELEASE_JSON="$API_RESPONSE_BODY"
  RELEASE_TAG="$(jq -r '.tag_name // empty' <<<"$SOURCE_RELEASE_JSON")"
  RELEASE_NAME="$(jq -r '.name // ""' <<<"$SOURCE_RELEASE_JSON")"
  RELEASE_BODY="$(jq -r '.body // ""' <<<"$SOURCE_RELEASE_JSON")"
  RELEASE_DRAFT="$(jq -r 'if .draft then "true" else "false" end' <<<"$SOURCE_RELEASE_JSON")"
  RELEASE_PRERELEASE="$(jq -r 'if .prerelease then "true" else "false" end' <<<"$SOURCE_RELEASE_JSON")"
  RELEASE_TARGET_COMMITISH="$(jq -r '.target_commitish // ""' <<<"$SOURCE_RELEASE_JSON")"
  RELEASE_MAKE_LATEST="$(jq -r '.make_latest // empty' <<<"$SOURCE_RELEASE_JSON")"

  if [[ -z "$RELEASE_TAG" ]]; then
    fail "source release tag is empty"
  fi

  log "GitHub Release: $RELEASE_TAG"
}

download_source_assets() {
  local asset_json asset_id asset_name asset_size asset_url asset_path actual_size

  while IFS= read -r asset_json; do
    [[ -z "$asset_json" ]] && continue

    asset_id="$(jq -r '.id' <<<"$asset_json")"
    asset_name="$(jq -r '.name' <<<"$asset_json")"
    asset_size="$(jq -r '.size' <<<"$asset_json")"
    asset_url="$(jq -r '.url' <<<"$asset_json")"
    asset_path="$WORK_DIR/assets/$asset_id"

    log "下载 GitHub 附件: $asset_name"
    curl \
      --silent \
      --show-error \
      --location \
      --fail \
      -H "Authorization: Bearer $GITHUB_TOKEN" \
      -H "Accept: application/octet-stream" \
      -H "X-GitHub-Api-Version: 2022-11-28" \
      --output "$asset_path" \
      "$asset_url"

    actual_size="$(wc -c < "$asset_path" | tr -d ' ')"
    if [[ "$actual_size" != "$asset_size" ]]; then
      fail "downloaded asset size mismatch for $asset_name: expected $asset_size, got $actual_size"
    fi

    SOURCE_ASSET_NAMES+=("$asset_name")
    SOURCE_ASSET_PATHS+=("$asset_path")
    SOURCE_ASSET_SIZES+=("$asset_size")
  done < <(jq -c '.assets[]?' <<<"$SOURCE_RELEASE_JSON")

  log "GitHub 附件数量: ${#SOURCE_ASSET_NAMES[@]}"
}

build_codeberg_payload() {
  jq -cn \
    --arg tag_name "$RELEASE_TAG" \
    --arg name "$RELEASE_NAME" \
    --arg body "$RELEASE_BODY" \
    --arg target_commitish "$RELEASE_TARGET_COMMITISH" \
    --argjson draft "$RELEASE_DRAFT" \
    --argjson prerelease "$RELEASE_PRERELEASE" '
      {
        tag_name: $tag_name,
        name: $name,
        body: $body,
        draft: $draft,
        prerelease: $prerelease
      }
      + (if $target_commitish == "" then {} else {target_commitish: $target_commitish} end)
    '
}

build_cnb_create_payload() {
  jq -cn \
    --arg tag_name "$RELEASE_TAG" \
    --arg name "$RELEASE_NAME" \
    --arg body "$RELEASE_BODY" \
    --arg target_commitish "$RELEASE_TARGET_COMMITISH" \
    --arg make_latest "$RELEASE_MAKE_LATEST" \
    --argjson draft "$RELEASE_DRAFT" \
    --argjson prerelease "$RELEASE_PRERELEASE" '
      {
        tag_name: $tag_name,
        name: $name,
        body: $body,
        draft: $draft,
        prerelease: $prerelease
      }
      + (if $target_commitish == "" then {} else {target_commitish: $target_commitish} end)
      + (if $make_latest == "" then {} else {make_latest: $make_latest} end)
    '
}

build_cnb_update_payload() {
  jq -cn \
    --arg name "$RELEASE_NAME" \
    --arg body "$RELEASE_BODY" \
    --arg make_latest "$RELEASE_MAKE_LATEST" \
    --argjson draft "$RELEASE_DRAFT" \
    --argjson prerelease "$RELEASE_PRERELEASE" '
      {
        name: $name,
        body: $body,
        draft: $draft,
        prerelease: $prerelease
      }
      + (if $make_latest == "" then {} else {make_latest: $make_latest} end)
    '
}

codeberg_release_assets_path() {
  local release_id="$1"
  printf '/repos/%s/%s/releases/%s/assets' \
    "$CODEBERG_OWNER_URI" \
    "$CODEBERG_REPO_NAME_URI" \
    "$(urlencode "$release_id")"
}

get_or_create_codeberg_release() {
  local tag_uri payload release_path
  tag_uri="$(urlencode "$RELEASE_TAG")"
  release_path="/repos/${CODEBERG_OWNER_URI}/${CODEBERG_REPO_NAME_URI}/releases/tags/${tag_uri}"

  codeberg_api GET "$release_path"

  if [[ "$API_RESPONSE_STATUS" == "404" ]]; then
    payload="$(build_codeberg_payload)"
    codeberg_api \
      POST \
      "/repos/${CODEBERG_OWNER_URI}/${CODEBERG_REPO_NAME_URI}/releases" \
      -H "Content-Type: application/json" \
      --data "$payload"
    expect_status "$API_RESPONSE_STATUS" 201
    CODEBERG_RELEASE_JSON="$API_RESPONSE_BODY"
    log "Codeberg Release 已创建: $RELEASE_TAG"
    return
  fi

  expect_status "$API_RESPONSE_STATUS" 200
  CODEBERG_RELEASE_JSON="$API_RESPONSE_BODY"
  CODEBERG_RELEASE_ID="$(jq -r '.id' <<<"$CODEBERG_RELEASE_JSON")"
  payload="$(build_codeberg_payload)"

  codeberg_api \
    PATCH \
    "/repos/${CODEBERG_OWNER_URI}/${CODEBERG_REPO_NAME_URI}/releases/${CODEBERG_RELEASE_ID}" \
    -H "Content-Type: application/json" \
    --data "$payload"
  expect_status "$API_RESPONSE_STATUS" 200
  CODEBERG_RELEASE_JSON="$API_RESPONSE_BODY"
  log "Codeberg Release 已更新: $RELEASE_TAG"
}

list_codeberg_assets() {
  local release_id="$1"
  codeberg_api GET "$(codeberg_release_assets_path "$release_id")"
  expect_status "$API_RESPONSE_STATUS" 200
  printf '%s' "$API_RESPONSE_BODY"
}

delete_codeberg_asset() {
  local release_id="$1"
  local asset_id="$2"

  codeberg_api DELETE "$(codeberg_release_assets_path "$release_id")/$(urlencode "$asset_id")"
  expect_status "$API_RESPONSE_STATUS" 204
}

upload_codeberg_asset() {
  local release_id="$1"
  local asset_name="$2"
  local asset_path="$3"
  local response_file http_code upload_url

  response_file="$(mktemp)"
  upload_url="${CODEBERG_API_BASE_URL%/}$(codeberg_release_assets_path "$release_id")?name=$(urlencode "$asset_name")"

  http_code="$(
    curl \
      --silent \
      --show-error \
      --location \
      --request POST \
      -H "Authorization: token $CODEBERG_TOKEN" \
      -H "Accept: application/json" \
      -F "attachment=@${asset_path};filename=${asset_name}" \
      --output "$response_file" \
      --write-out '%{http_code}' \
      "$upload_url"
  )"
  API_RESPONSE_BODY="$(cat "$response_file")"
  rm -f "$response_file"
  expect_status "$http_code" 201
}

sync_codeberg_assets() {
  local release_id="$1"
  local assets_json existing_id asset_json asset_id asset_name
  local index asset_path

  assets_json="$(list_codeberg_assets "$release_id")"

  for index in "${!SOURCE_ASSET_NAMES[@]}"; do
    asset_name="${SOURCE_ASSET_NAMES[$index]}"
    asset_path="${SOURCE_ASSET_PATHS[$index]}"
    existing_id="$(jq -r --arg name "$asset_name" '.[] | select(.name == $name) | .id' <<<"$assets_json" | head -n 1)"

    if [[ -n "$existing_id" ]]; then
      log "删除 Codeberg 已有附件: $asset_name"
      delete_codeberg_asset "$release_id" "$existing_id"
      assets_json="$(list_codeberg_assets "$release_id")"
    fi

    log "上传 Codeberg 附件: $asset_name"
    upload_codeberg_asset "$release_id" "$asset_name" "$asset_path"
    assets_json="$(list_codeberg_assets "$release_id")"
  done

  while IFS= read -r asset_json; do
    [[ -z "$asset_json" ]] && continue

    asset_name="$(jq -r '.name' <<<"$asset_json")"
    asset_id="$(jq -r '.id' <<<"$asset_json")"

    if ! source_has_asset_name "$asset_name"; then
      log "清理 Codeberg 多余附件: $asset_name"
      delete_codeberg_asset "$release_id" "$asset_id"
    fi
  done < <(jq -c '.[]?' <<<"$assets_json")
}

get_or_create_cnb_release() {
  local tag_uri payload release_path
  tag_uri="$(urlencode "$RELEASE_TAG")"
  release_path="${CNB_REPO_PATH}/-/releases/tags/${tag_uri}"

  cnb_api GET "$release_path"

  if [[ "$API_RESPONSE_STATUS" == "404" ]]; then
    payload="$(build_cnb_create_payload)"
    cnb_api \
      POST \
      "${CNB_REPO_PATH}/-/releases" \
      -H "Content-Type: application/json" \
      --data "$payload"
    expect_status "$API_RESPONSE_STATUS" 201
    CNB_RELEASE_JSON="$API_RESPONSE_BODY"
    log "CNB Release 已创建: $RELEASE_TAG"
    return
  fi

  expect_status "$API_RESPONSE_STATUS" 200
  CNB_RELEASE_JSON="$API_RESPONSE_BODY"
  CNB_RELEASE_ID="$(jq -r '.id' <<<"$CNB_RELEASE_JSON")"
  payload="$(build_cnb_update_payload)"

  cnb_api \
    PATCH \
    "${CNB_REPO_PATH}/-/releases/$(urlencode "$CNB_RELEASE_ID")" \
    -H "Content-Type: application/json" \
    --data "$payload"
  expect_status "$API_RESPONSE_STATUS" 200

  cnb_api GET "${CNB_REPO_PATH}/-/releases/$(urlencode "$CNB_RELEASE_ID")"
  expect_status "$API_RESPONSE_STATUS" 200
  CNB_RELEASE_JSON="$API_RESPONSE_BODY"
  log "CNB Release 已更新: $RELEASE_TAG"
}

delete_cnb_asset() {
  local release_id="$1"
  local asset_id="$2"

  cnb_api DELETE "${CNB_REPO_PATH}/-/releases/$(urlencode "$release_id")/assets/$(urlencode "$asset_id")"
  expect_status "$API_RESPONSE_STATUS" 200
}

upload_cnb_asset() {
  local release_id="$1"
  local asset_name="$2"
  local asset_path="$3"
  local asset_size="$4"
  local upload_payload upload_url verify_url upload_status verify_target

  upload_payload="$(jq -cn \
    --arg asset_name "$asset_name" \
    --argjson size "$asset_size" \
    '{asset_name: $asset_name, overwrite: true, size: $size, ttl: 0}'
  )"

  cnb_api \
    POST \
    "${CNB_REPO_PATH}/-/releases/$(urlencode "$release_id")/asset-upload-url" \
    -H "Content-Type: application/json" \
    --data "$upload_payload"
  expect_status "$API_RESPONSE_STATUS" 201

  upload_url="$(jq -r '.upload_url // empty' <<<"$API_RESPONSE_BODY")"
  verify_url="$(jq -r '.verify_url // empty' <<<"$API_RESPONSE_BODY")"

  if [[ -z "$upload_url" || -z "$verify_url" ]]; then
    fail "CNB upload url response is incomplete: ${API_RESPONSE_BODY:-<empty>}"
  fi

  upload_url="$(absolute_url "$CNB_API_BASE_URL" "$upload_url")"
  verify_target="$(append_query_param "$(absolute_url "$CNB_API_BASE_URL" "$verify_url")" "ttl=0")"

  upload_status="$(
    curl \
      --silent \
      --show-error \
      --location \
      --request PUT \
      --upload-file "$asset_path" \
      --output /dev/null \
      --write-out '%{http_code}' \
      "$upload_url"
  )"

  case "$upload_status" in
    200|201|204)
      ;;
    *)
      fail "unexpected CNB upload status: $upload_status for $asset_name"
      ;;
  esac

  api_request \
    POST \
    "$verify_target" \
    "Bearer $CNB_TOKEN" \
    -H "Accept: application/vnd.cnb.api+json"
  expect_status "$API_RESPONSE_STATUS" 200
}

sync_cnb_assets() {
  local release_id="$1"
  local release_json asset_json asset_id asset_name
  local index asset_path asset_size

  release_json="$CNB_RELEASE_JSON"

  while IFS= read -r asset_json; do
    [[ -z "$asset_json" ]] && continue

    asset_name="$(jq -r '.name' <<<"$asset_json")"
    asset_id="$(jq -r '.id' <<<"$asset_json")"

    if ! source_has_asset_name "$asset_name"; then
      log "清理 CNB 多余附件: $asset_name"
      delete_cnb_asset "$release_id" "$asset_id"
    fi
  done < <(jq -c '.assets[]?' <<<"$release_json")

  for index in "${!SOURCE_ASSET_NAMES[@]}"; do
    asset_name="${SOURCE_ASSET_NAMES[$index]}"
    asset_path="${SOURCE_ASSET_PATHS[$index]}"
    asset_size="${SOURCE_ASSET_SIZES[$index]}"

    log "上传 CNB 附件: $asset_name"
    upload_cnb_asset "$release_id" "$asset_name" "$asset_path" "$asset_size"
  done
}

main() {
  require_env SOURCE_RELEASE_ID
  require_env GITHUB_REPOSITORY
  require_env GITHUB_TOKEN
  require_env CODEBERG_TOKEN
  require_env CNB_TOKEN

  GITHUB_API_URL="${GITHUB_API_URL:-https://api.github.com}"
  CODEBERG_API_BASE_URL="${CODEBERG_API_BASE_URL:-https://codeberg.org/api/v1}"
  CNB_API_BASE_URL="${CNB_API_BASE_URL:-https://api.cnb.cool}"

  SOURCE_REPOSITORY="${SOURCE_REPOSITORY:-$GITHUB_REPOSITORY}"
  CODEBERG_REPO="${CODEBERG_REPO:-$GITHUB_REPOSITORY}"
  CNB_REPO="${CNB_REPO:-$GITHUB_REPOSITORY}"

  parse_repo_slug "$SOURCE_REPOSITORY"
  parse_repo_slug "$CODEBERG_REPO"
  parse_repo_slug "$CNB_REPO"

  SOURCE_OWNER="${SOURCE_REPOSITORY%%/*}"
  SOURCE_REPO_NAME="${SOURCE_REPOSITORY#*/}"
  SOURCE_OWNER_URI="$(urlencode "$SOURCE_OWNER")"
  SOURCE_REPO_NAME_URI="$(urlencode "$SOURCE_REPO_NAME")"

  CODEBERG_OWNER="${CODEBERG_REPO%%/*}"
  CODEBERG_REPO_NAME="${CODEBERG_REPO#*/}"
  CODEBERG_OWNER_URI="$(urlencode "$CODEBERG_OWNER")"
  CODEBERG_REPO_NAME_URI="$(urlencode "$CODEBERG_REPO_NAME")"
  CNB_OWNER="${CNB_REPO%%/*}"
  CNB_REPO_NAME="${CNB_REPO#*/}"
  CNB_OWNER_URI="$(urlencode "$CNB_OWNER")"
  CNB_REPO_NAME_URI="$(urlencode "$CNB_REPO_NAME")"
  CNB_REPO_PATH="/${CNB_OWNER_URI}/${CNB_REPO_NAME_URI}"

  WORK_DIR="$(mktemp -d)"
  mkdir -p "$WORK_DIR/assets"
  trap 'rm -rf "$WORK_DIR"' EXIT

  declare -ga SOURCE_ASSET_NAMES=()
  declare -ga SOURCE_ASSET_PATHS=()
  declare -ga SOURCE_ASSET_SIZES=()

  download_source_release
  download_source_assets

  get_or_create_codeberg_release
  CODEBERG_RELEASE_ID="$(jq -r '.id' <<<"$CODEBERG_RELEASE_JSON")"
  if [[ -z "$CODEBERG_RELEASE_ID" ]]; then
    fail "failed to resolve Codeberg release id"
  fi
  sync_codeberg_assets "$CODEBERG_RELEASE_ID"

  get_or_create_cnb_release
  CNB_RELEASE_ID="$(jq -r '.id' <<<"$CNB_RELEASE_JSON")"
  if [[ -z "$CNB_RELEASE_ID" ]]; then
    fail "failed to resolve CNB release id"
  fi
  sync_cnb_assets "$CNB_RELEASE_ID"

  log "Release 同步完成: GitHub -> Codeberg, CNB"
}

main "$@"
