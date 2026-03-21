---
title: NetEase Cloud Music API
---

# NetEase Cloud Music API {#netease-cloud-music-api}

## パブリックサーバー {#public-servers}

::: warning
**パブリックサーバーでのログイン操作は慎重に行ってください。**

**パブリックサーバーの使用によるアカウント盗難などの問題については責任を負いません。**
:::

<NeteaseApiTable />

## 設定方法 {#configuration}

### V4

```toml {4}
# API設定
[api]
# NetEase Cloud Music
netease-link = "https://ncm.zhenxin.me"
```

### V2 {#v2}

```json {8}
/// API設定
  "api": {
    /// NetEase Cloud Music APIアドレス
    "netease": "https://ncm.zhenxin.me"
  },
```

## セルフホスト {#self-hosted}

- [標準版デプロイ](/ja/netease-api/standard) - NeteaseCloudMusicApi ベース
- [拡張版デプロイ](/ja/netease-api/enhanced) - NeteaseCloudMusicApiEnhanced ベース、楽曲アンロックとロスレス音質をサポート
