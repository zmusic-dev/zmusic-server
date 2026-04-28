---
title: 常见问题
---

# 常见问题 {#faq}

::: warning
**此处的常见问题仅适用于 V4 版本**

**V2 版本请查看 [常见问题 (V2 留档)](/v2/faq)**
:::

### 为什么播放、搜索、停止和歌单命令提示暂不可用？ {#why-are-music-commands-unavailable}

V4 目前已完成基础命令和权限控制，但音乐功能仍在开发中。

因此 `/zmusic play`、`/zmusic search`、`/zmusic stop`、`/zmusic playlist` 及其子命令当前会提示功能暂不可用。

### 现在有哪些命令可以使用？ {#available-commands}

当前可用命令包括：

- `/zmusic help [command]`
- `/zmusic info`
- `/zmusic reload`

根命令别名包括 `/music` 和 `/zm`。

### 我的服务器没有网络，无法使用 ZMusic？ {#no-network}

当前可用的基础命令不依赖音乐 API 网络连接。

后续音乐播放、搜索、歌词显示和歌单功能完成后，相关功能会需要访问配置的音乐 API 服务。

### V4 是否支持网易云音乐登录？ {#netease-login}

暂不支持。当前 V4 中没有 `/zm login` 或等价登录命令。

### 网易云 API 文档现在有什么用途？ {#netease-api-docs-purpose}

网易云 API 文档用于说明可部署的外部 API 服务。V4 音乐功能尚未接入这些服务，因此它们目前是后续功能开发的准备资料。
