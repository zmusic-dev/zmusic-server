---
title: Netease Cloud Music API
---

# Netease Cloud Music API

## Public Servers

::: warning
**Be careful when using public servers to perform login operations.**

**We are not responsible for account theft or other issues caused by public servers.**
:::

<NeteaseApiTable />

::: warning
If you cannot log in when using a self-hosted API,

please make sure you followed the deployment guide below and switched the login method to email login.
:::

## Replace Location

### V4

```toml {4}
# API settings
[api]
# Netease Cloud Music
netease-link = "https://ncm.zhenxin.me"
```

### V2

```json {8}
/// API settings
  "api": {
    /// Netease Cloud Music API address
    ///
    /// Uses the open-source project NeteaseCloudMusicApi
    /// Self-hosting is recommended and requires Node.js
    /// URL: https://github.com/Binaryify/NeteaseCloudMusicApi
    "netease": "https://ncm.zhenxin.me"
  },
```

## Deployment

Make sure the following software is installed before deployment:

- [Git](https://git-scm.com/download)
- [Node.js](https://nodejs.org/)

1. Get the source code

```shell
wget https://registry.npmmirror.com/NeteaseCloudMusicApi/-/NeteaseCloudMusicApi-4.27.0.tgz
tar -xzvf NeteaseCloudMusicApi-4.27.0.tgz
mv package NeteaseCloudMusicApi
cd NeteaseCloudMusicApi
```

2. Install dependencies

```shell
npm install
```

3. Run the server

```shell
node app.js
```

4. Customize the port

4.1 Linux

```shell
PORT=4000 node app.js
```

4.2 Windows

```bat
set PORT=4000 && node app.js
```
