---
title: NetEase Cloud Music API 標準版デプロイ
---

# NetEase Cloud Music API 標準版デプロイ {#netease-cloud-music-api-standard}

[NeteaseCloudMusicApi](https://github.com/Binaryify/NeteaseCloudMusicApi) プロジェクトベース。

## 環境要件 {#requirements}

- [Node.js](https://nodejs.org/) 14+

## 推奨方式 {#recommended-methods}

| 方式 | 向いている用途 | 入口 |
| --- | --- | --- |
| `npm install` | 長期運用・依存固定あり | `npm install NeteaseCloudMusicApi@latest` |
| `npx` | クイック検証 | `npx NeteaseCloudMusicApi@latest` |
| `bunx` | Bun でのワンショット起動 | `bunx NeteaseCloudMusicApi@latest` |
| Docker Compose | コンテナでの長期運用 | `oven/bun:alpine + bunx NeteaseCloudMusicApi` |

::: note
`npm install` は長期運用向きです。`npx` / `bunx` は初回実行時にパッケージを取得するため、クイック検証向きです。
:::

## NPM パッケージデプロイ {#npm-install-deployment}

### 1. 作業ディレクトリを初期化

```shell
mkdir netease-cloud-music-api
cd netease-cloud-music-api
npm init -y
```

### 2. 依存関係をインストール

```shell
npm install NeteaseCloudMusicApi@latest
```

### 3. 実行

```shell
npx NeteaseCloudMusicApi
```

### 4. ポートのカスタマイズ

**Linux/macOS**

```shell
PORT=4000 npx NeteaseCloudMusicApi
```

**Windows**

```bat
set PORT=4000 && npx NeteaseCloudMusicApi
```

## NPX / Bunx クイック起動 {#npx-bunx}

**デフォルトポート 3000**

```shell
npx NeteaseCloudMusicApi@latest
```

```shell
bunx NeteaseCloudMusicApi@latest
```

**ポート変更**

```shell
PORT=4000 npx NeteaseCloudMusicApi@latest
```

```shell
PORT=4000 bunx NeteaseCloudMusicApi@latest
```

**Windows**

```bat
set PORT=4000 && npx NeteaseCloudMusicApi@latest
set PORT=4000 && bunx NeteaseCloudMusicApi@latest
```

## Docker Compose デプロイ {#docker}

ここでは Bun ベースイメージ内で `bunx NeteaseCloudMusicApi` を直接実行する構成を使います。古いプリビルドイメージに依存しません。

```yaml
services:
  ncm-api:
    image: oven/bun:alpine
    container_name: ncm-api
    restart: always
    environment:
      - TZ=Asia/Shanghai
    ports:
      - "127.0.0.1:3000:3000"
    command: bunx NeteaseCloudMusicApi
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
