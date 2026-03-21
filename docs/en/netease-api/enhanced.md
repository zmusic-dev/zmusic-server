---
title: Netease Cloud Music API Enhanced Deployment
---

# Netease Cloud Music API Enhanced Deployment {#netease-cloud-music-api-enhanced}

Based on [NeteaseCloudMusicApiEnhanced/api-enhanced](https://github.com/NeteaseCloudMusicApiEnhanced/api-enhanced) project.

## Features {#features}

- Song unlocking (ungraying)
- Lossless audio (FLAC) support
- Community-maintained

## Requirements {#requirements}

- [Node.js](https://nodejs.org/) 18+
- pnpm is recommended for dependency management

## Recommended Methods {#recommended-methods}

| Method | Best for | Entry point |
| --- | --- | --- |
| `git clone` | Long-running self-hosted deployment | `git clone https://github.com/NeteaseCloudMusicApiEnhanced/api-enhanced.git` |
| `npx` | Quick trial run | `npx @neteasecloudmusicapienhanced/api@latest` |
| `bunx` | One-shot startup with Bun | `bunx @neteasecloudmusicapienhanced/api@latest` |
| Docker Compose | Long-running container deployment | `oven/bun:alpine + bunx @neteasecloudmusicapienhanced/api` |

::: note
`npx` / `bunx` rely on the executable entry exported by the official npm package `@neteasecloudmusicapienhanced/api`. They download the package on first run, so `git clone` or Docker is still the better choice for persistent deployment.
:::

## Git Clone Deployment {#git-clone-deployment}

### 1. Get the source code

```shell
git clone https://github.com/NeteaseCloudMusicApiEnhanced/api-enhanced.git
cd api-enhanced
```

### 2. Install dependencies

```shell
pnpm install
```

### 3. Run

```shell
node app.js
```

### 4. Customize port

**Linux/macOS**

```shell
PORT=4000 node app.js
```

**Windows**

```bat
set PORT=4000 && node app.js
```

## NPX / Bunx Quick Start {#npx-bunx}

**Default port 3000**

```shell
npx @neteasecloudmusicapienhanced/api@latest
```

```shell
bunx @neteasecloudmusicapienhanced/api@latest
```

**Custom port**

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

## Environment Variables {#environment-variables}

| Variable | Default | Description |
| --- | --- | --- |
| `CORS_ALLOW_ORIGIN` | `*` | Allowed CORS origins. Multiple origins can be separated by commas. |
| `ENABLE_PROXY` | `false` | Whether to enable reverse proxy support. |
| `PROXY_URL` | `https://your-proxy-url.com/?proxy=` | Reverse proxy URL, only used when `ENABLE_PROXY=true`. |
| `ENABLE_GENERAL_UNBLOCK` | `true` | Enable global song unlocking |
| `ENABLE_FLAC` | `true` | Enable lossless audio |
| `SELECT_MAX_BR` | `false` | Select max bitrate when FLAC is enabled |
| `FOLLOW_SOURCE_ORDER` | `true` | Match tracks strictly in source order. |

## Docker Compose {#docker}

This setup runs `bunx @neteasecloudmusicapienhanced/api` directly inside a Bun base image, which keeps the container definition simple and easy to update.

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

Start it with:

```shell
docker compose up -d
```

If you are not using 1Panel, replace `1panel-network` with your own network name, or remove the `networks` section to let Compose create the default network.

## FAQ {#faq}

::: warning Login failed
If you encounter login issues, please try using email login.
:::
