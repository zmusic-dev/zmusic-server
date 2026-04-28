---
title: 网易云音乐 API
---

# 网易云音乐 API {#netease-cloud-music-api}

::: info V4 状态
当前 V4 音乐功能仍在开发中，尚未接入网易云音乐 API。此页面用于说明后续可使用的外部 API 服务与配置方式。
:::

## 公共服务器 {#public-servers}

::: warning
**请谨慎使用公共服务器执行登录操作。**

**因使用公共服务器所导致的盗号等问题，概不负责。**
:::

<NeteaseApiTable />

## 配置方式 {#configuration}

### V4

```toml {4}
# API设置
[api]
# 网易云音乐
netease-link = "https://ncm.zhenxin.me"
```

### V2 {#v2}

```json {8}
/// API设置
  "api": {
    /// 网易云音乐API地址
    "netease": "https://ncm.zhenxin.me"
  },
```

## 自建服务 {#self-hosted}

- [标准版部署](/netease-api/standard) - 基于 NeteaseCloudMusicApi
- [增强版部署](/netease-api/enhanced) - 基于 NeteaseCloudMusicApiEnhanced，支持歌曲解锁和无损音质
