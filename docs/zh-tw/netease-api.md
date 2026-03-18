---
title: 網易雲音樂 API
---

# 網易雲音樂 API

## 公共伺服器

::: warning
**請謹慎使用公共伺服器執行登入操作。**

**因使用公共伺服器所導致的盜號等問題，概不負責。**
:::

<NeteaseApiTable />

::: warning
如果遇到自建 API 無法登入的問題。

請確保您遵循了下方的部署指南，並且將登入方式改為郵箱登入。
:::

## 替換位置

### V4

```toml {4}
# API設定
[api]
# 網易雲音樂
netease-link = "https://ncm.zhenxin.me"
```

### V2

```json {8}
/// API設定
  "api": {
    /// 網易雲音樂API地址
    ///
    /// 使用開源專案NeteaseCloudMusicApi
    /// 推薦自行部署，需Node.js環境
    /// 地址: https://github.com/Binaryify/NeteaseCloudMusicApi
    "netease": "https://ncm.zhenxin.me"
  },
```

## 部署

部署前確保已安裝如下環境

- [Git](https://git-scm.com/download)
- [Node.js](https://nodejs.org/zh-cn/)

1. 獲取代碼

```shell
wget https://registry.npmmirror.com/NeteaseCloudMusicApi/-/NeteaseCloudMusicApi-4.27.0.tgz
tar -xzvf NeteaseCloudMusicApi-4.27.0.tgz
mv package NeteaseCloudMusicApi
cd NeteaseCloudMusicApi
```

2. 安裝依賴

```shell
npm install
```

3. 運行

```shell
node app.js
```

4. 自定義連接埠

4.1 Linux

```shell
PORT=4000 node app.js
```

4.2 Windows

```bat
set PORT=4000 && node app.js
```
