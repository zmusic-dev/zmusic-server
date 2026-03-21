---
title: Netease Cloud Music API Standard Deployment
---

# Netease Cloud Music API Standard Deployment {#netease-cloud-music-api-standard}

Based on [NeteaseCloudMusicApi](https://github.com/Binaryify/NeteaseCloudMusicApi) project.

## Requirements {#requirements}

- [Node.js](https://nodejs.org/) 14+

## Recommended Methods {#recommended-methods}

| Method | Best for | Entry point |
| --- | --- | --- |
| `npm install` | Persistent deployment with pinned dependencies | `npm install NeteaseCloudMusicApi@latest` |
| `npx` | Quick trial run | `npx NeteaseCloudMusicApi@latest` |
| `bunx` | One-shot startup with Bun | `bunx NeteaseCloudMusicApi@latest` |
| Docker Compose | Long-running container deployment | `oven/bun:alpine + bunx NeteaseCloudMusicApi` |

::: note
`npm install` is the better fit for persistent deployment. `npx` / `bunx` download the package on first run and are better suited to quick validation.
:::

## NPM Package Deployment {#npm-install-deployment}

### 1. Initialize a working directory

```shell
mkdir netease-cloud-music-api
cd netease-cloud-music-api
npm init -y
```

### 2. Install dependencies

```shell
npm install NeteaseCloudMusicApi@latest
```

### 3. Run

```shell
npx NeteaseCloudMusicApi
```

### 4. Customize port

**Linux/macOS**

```shell
PORT=4000 npx NeteaseCloudMusicApi
```

**Windows**

```bat
set PORT=4000 && npx NeteaseCloudMusicApi
```

## NPX / Bunx Quick Start {#npx-bunx}

**Default port 3000**

```shell
npx NeteaseCloudMusicApi@latest
```

```shell
bunx NeteaseCloudMusicApi@latest
```

**Custom port**

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

## Docker Compose {#docker}

This setup runs `bunx NeteaseCloudMusicApi` directly inside a Bun base image, so you do not need to rely on an old prebuilt image.

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

Start it with:

```shell
docker compose up -d
```

If you are not using 1Panel, replace `1panel-network` with your own network name, or remove the `networks` section to let Compose create the default network.

## FAQ {#faq}

::: warning Login failed
If you encounter login issues, please try using email login.
:::
