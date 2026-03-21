---
title: 网易云音乐 API 标准版部署
---

# 网易云音乐 API 标准版部署 {#netease-cloud-music-api-standard}

基于 [NeteaseCloudMusicApi](https://github.com/Binaryify/NeteaseCloudMusicApi) 项目。

## 环境要求 {#requirements}

- [Node.js](https://nodejs.org/zh-cn/) 14+

## 推荐方式 {#recommended-methods}

| 方式 | 适合场景 | 入口 |
| --- | --- | --- |
| `npm install` | 长期运行、希望固定依赖 | `npm install NeteaseCloudMusicApi@latest` |
| `npx` | 快速试跑 | `npx NeteaseCloudMusicApi@latest` |
| `bunx` | 使用 Bun 快速启动 | `bunx NeteaseCloudMusicApi@latest` |
| Docker Compose | 容器化长期运行 | `oven/bun:alpine + bunx NeteaseCloudMusicApi` |

::: note
`npm install` 更适合长期部署；`npx` / `bunx` 首次运行会临时下载包，更适合快速验证。
:::

## NPM 安装部署 {#npm-install-deployment}

### 1. 初始化目录

```shell
mkdir netease-cloud-music-api
cd netease-cloud-music-api
npm init -y
```

### 2. 安装依赖

```shell
npm install NeteaseCloudMusicApi@latest
```

### 3. 运行

```shell
npx NeteaseCloudMusicApi
```

### 4. 自定义端口

**Linux/macOS**

```shell
PORT=4000 npx NeteaseCloudMusicApi
```

**Windows**

```bat
set PORT=4000 && npx NeteaseCloudMusicApi
```

## NPX / Bunx 快速运行 {#npx-bunx}

**默认端口 3000**

```shell
npx NeteaseCloudMusicApi@latest
```

```shell
bunx NeteaseCloudMusicApi@latest
```

**自定义端口**

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

## Docker Compose 部署 {#docker}

这里改用 Bun 基础镜像在容器内直接执行 `bunx NeteaseCloudMusicApi`，避免依赖长期未更新的旧镜像。

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

启动命令：

```shell
docker compose up -d
```

如果你不是在 1Panel 下部署，可以把 `1panel-network` 替换成自己的网络名，或者直接删除 `networks` 配置让 Compose 使用默认网络。

## 常见问题 {#faq}

::: warning 登录失败
如果遇到无法登录的问题，请尝试使用邮箱登录。
:::
