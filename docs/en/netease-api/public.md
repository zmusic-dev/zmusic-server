---
title: Netease Cloud Music API
---

# Netease Cloud Music API {#netease-cloud-music-api}

::: info V4R status
The V4R music backend is still under development and is not connected to the Netease Cloud Music API yet. This page documents external API services and configuration for future use.
:::

## Public Servers {#public-servers}

::: warning
**Be careful when using public servers to perform login operations.**

**We are not responsible for account theft or other issues caused by public servers.**
:::

<NeteaseApiTable />

## Configuration {#configuration}

### V4

```toml {4}
# API settings
[api]
# Netease Cloud Music
netease-link = "https://ncm.zhenxin.me"
```

### V2 {#v2}

```json {8}
/// API settings
  "api": {
    /// Netease Cloud Music API address
    "netease": "https://ncm.zhenxin.me"
  },
```

## Self-hosted {#self-hosted}

- [Standard Deployment](/en/netease-api/standard) - Based on NeteaseCloudMusicApi
- [Enhanced Deployment](/en/netease-api/enhanced) - Based on NeteaseCloudMusicApiEnhanced, supports song unlocking and lossless audio
