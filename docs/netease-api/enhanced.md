---
title: 网易云音乐 API 增强版部署
---

# 网易云音乐 API 增强版部署 {#netease-cloud-music-api-enhanced}

基于 [NeteaseCloudMusicApiEnhanced/api-enhanced](https://github.com/NeteaseCloudMusicApiEnhanced/api-enhanced) 项目。

## 特性 {#features}

- 歌曲解锁（解灰）
- 无损音质（FLAC）支持
- 社区持续维护

## 环境要求 {#requirements}

- [Node.js](https://nodejs.org/) 18+
- 推荐使用 pnpm 管理依赖

## 推荐方式 {#recommended-methods}

| 方式 | 适合场景 | 入口 |
| --- | --- | --- |
| `git clone` | 长期运行、需要改配置 | `git clone https://github.com/NeteaseCloudMusicApiEnhanced/api-enhanced.git` |
| `npx` | 快速试跑 | `npx @neteasecloudmusicapienhanced/api@latest` |
| `bunx` | 使用 Bun 快速启动 | `bunx @neteasecloudmusicapienhanced/api@latest` |
| Docker Compose | 容器化长期运行 | `oven/bun:alpine + bunx @neteasecloudmusicapienhanced/api` |

::: note
`npx` / `bunx` 方式依赖官方 npm 包 `@neteasecloudmusicapienhanced/api` 的可执行入口，首次运行会临时下载包；长期部署更推荐 `git clone` 或 Docker。
:::

## Git Clone 部署 {#git-clone-deployment}

### 1. 获取代码

```shell
git clone https://github.com/NeteaseCloudMusicApiEnhanced/api-enhanced.git
cd api-enhanced
```

### 2. 安装依赖

```shell
pnpm install
```

### 3. 运行

```shell
node app.js
```

### 4. 自定义端口

**Linux/macOS**

```shell
PORT=4000 node app.js
```

**Windows**

```bat
set PORT=4000 && node app.js
```

## NPX / Bunx 快速运行 {#npx-bunx}

**默认端口 3000**

```shell
npx @neteasecloudmusicapienhanced/api@latest
```

```shell
bunx @neteasecloudmusicapienhanced/api@latest
```

**自定义端口**

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

## 环境变量 {#environment-variables}

| 变量名 | 默认值 | 说明 |
| --- | --- | --- |
| `CORS_ALLOW_ORIGIN` | `*` | 允许跨域请求的域名，多个来源可用逗号分隔。 |
| `ENABLE_PROXY` | `false` | 是否启用反向代理功能。 |
| `PROXY_URL` | `https://your-proxy-url.com/?proxy=` | 反向代理地址，仅在 `ENABLE_PROXY=true` 时生效。 |
| `ENABLE_GENERAL_UNBLOCK` | `true` | 启用全局解灰 |
| `ENABLE_FLAC` | `true` | 启用无损音质 |
| `SELECT_MAX_BR` | `false` | 启用无损时选择最高码率 |
| `FOLLOW_SOURCE_ORDER` | `true` | 是否严格按音源列表顺序进行匹配。 |

## Docker Compose 部署 {#docker}

这里直接使用 Bun 基础镜像，通过 `bunx @neteasecloudmusicapienhanced/api` 启动服务，省去单独维护镜像版本。

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

启动命令：

```shell
docker compose up -d
```

如果你不是在 1Panel 下部署，可以把 `1panel-network` 替换成自己的网络名，或者直接删除 `networks` 配置让 Compose 使用默认网络。

## 常见问题 {#faq}

::: warning 登录失败
如果遇到无法登录的问题，请尝试使用邮箱登录。
:::
