---
title: NetEase Cloud Music API 拡張版デプロイ
---

# NetEase Cloud Music API 拡張版デプロイ {#netease-cloud-music-api-enhanced}

[NeteaseCloudMusicApiEnhanced/api-enhanced](https://github.com/NeteaseCloudMusicApiEnhanced/api-enhanced) プロジェクトベース。

## 特徴 {#features}

- 楽曲のアンロック（グレー解除）
- ロスレス音質（FLAC）サポート
- コミュニティによる継続的なメンテナンス

## 環境要件 {#requirements}

- [Node.js](https://nodejs.org/) 18+
- pnpm による依存関係管理を推奨

## 推奨方式 {#recommended-methods}

| 方式 | 向いている用途 | 入口 |
| --- | --- | --- |
| `git clone` | 長期運用・設定変更あり | `git clone https://github.com/NeteaseCloudMusicApiEnhanced/api-enhanced.git` |
| `npx` | クイック検証 | `npx @neteasecloudmusicapienhanced/api@latest` |
| `bunx` | Bun でのワンショット起動 | `bunx @neteasecloudmusicapienhanced/api@latest` |
| Docker Compose | コンテナでの長期運用 | `oven/bun:alpine + bunx @neteasecloudmusicapienhanced/api` |

::: note
`npx` / `bunx` は公式 npm パッケージ `@neteasecloudmusicapienhanced/api` の CLI 入口を使います。初回実行時にパッケージを取得するため、長期運用では `git clone` か Docker を推奨します。
:::

## Git Clone デプロイ {#git-clone-deployment}

### 1. コードを取得

```shell
git clone https://github.com/NeteaseCloudMusicApiEnhanced/api-enhanced.git
cd api-enhanced
```

### 2. 依存関係をインストール

```shell
pnpm install
```

### 3. 実行

```shell
node app.js
```

### 4. ポートのカスタマイズ

**Linux/macOS**

```shell
PORT=4000 node app.js
```

**Windows**

```bat
set PORT=4000 && node app.js
```

## NPX / Bunx クイック起動 {#npx-bunx}

**デフォルトポート 3000**

```shell
npx @neteasecloudmusicapienhanced/api@latest
```

```shell
bunx @neteasecloudmusicapienhanced/api@latest
```

**ポート変更**

```shell
PORT=4000 npx @neteasecloudmusicapienhanced/api@latest
```

```shell
PORT=4000 bunx @neteasecloudmusicapienhanced/api@latest
```

**Windows**

```bat
set PORT=4000 && npx @neteasecloudmusicapienhanced/api@latest
set PORT=4000 && bunx @neteasecloudmusicapienhanced/api@latest
```

## 環境変数 {#environment-variables}

| 変数名 | デフォルト値 | 説明 |
| --- | --- | --- |
| `CORS_ALLOW_ORIGIN` | `*` | 許可する CORS オリジン。複数指定はカンマ区切りです。 |
| `ENABLE_PROXY` | `false` | リバースプロキシ機能を有効にするかどうか。 |
| `PROXY_URL` | `https://your-proxy-url.com/?proxy=` | リバースプロキシ URL。`ENABLE_PROXY=true` のときだけ有効です。 |
| `ENABLE_GENERAL_UNBLOCK` | `true` | グローバル楽曲アンロックを有効化 |
| `ENABLE_FLAC` | `true` | ロスレス音質を有効化 |
| `SELECT_MAX_BR` | `false` | ロスレス有効時に最高ビットレートを選択 |
| `FOLLOW_SOURCE_ORDER` | `true` | 音源リスト順に厳密にマッチさせるかどうか。 |

## Docker Compose デプロイ {#docker}

ここでは Bun ベースイメージ内で `bunx @neteasecloudmusicapienhanced/api` を直接実行する構成を使います。更新時もイメージ差し替えが容易です。

```yaml
services:
  ncm-api-enhanced:
    image: oven/bun:alpine
    container_name: ncm-api-enhanced
    restart: always
    environment:
      - TZ=Asia/Shanghai
    ports:
      - "127.0.0.1:3003:3000"
    command: bunx @neteasecloudmusicapienhanced/api
    networks:
      - 1panel-network
    deploy:
      resources:
        limits:
          memory: 256M

networks:
  1panel-network:
    external: true
```

起動コマンド:

```shell
docker compose up -d
```

1Panel を使わない場合は `1panel-network` を自分のネットワーク名に置き換えるか、`networks` セクションを削除して Compose のデフォルトネットワークを使ってください。

## よくある質問 {#faq}

::: warning ログイン失敗
ログインできない問題が発生した場合は、メールアドレスログインをお試しください。
:::
