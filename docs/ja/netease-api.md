---
title: NetEase Cloud Music API
---

# NetEase Cloud Music API

## パブリックサーバー

::: warning
**パブリックサーバーでのログイン操作は慎重に行ってください。**

**パブリックサーバーの使用によるアカウント盗難などの問題については責任を負いません。**
:::

<NeteaseApiTable />

::: warning
自己構築した API でログインできない問題が発生した場合。

以下のデプロイガイドに従っていることを確認し、ログイン方法をメールアドレスログインに変更してください。
:::

## 置換場所

### V4

```toml {4}
# API設定
[api]
# NetEase Cloud Music
netease-link = "https://ncm.zhenxin.me"
```

### V2

```json {8}
/// API設定
  "api": {
    /// NetEase Cloud Music APIアドレス
    ///
    /// オープンソースプロジェクトNeteaseCloudMusicApiを使用
    /// 自己デプロイを推奨、Node.js環境が必要
    /// URL: https://github.com/Binaryify/NeteaseCloudMusicApi
    "netease": "https://ncm.zhenxin.me"
  },
```

## デプロイ

デプロイ前に以下の環境がインストールされていることを確認してください

- [Git](https://git-scm.com/download)
- [Node.js](https://nodejs.org/)

1. コードを取得

```shell
wget https://registry.npmmirror.com/NeteaseCloudMusicApi/-/NeteaseCloudMusicApi-4.27.0.tgz
tar -xzvf NeteaseCloudMusicApi-4.27.0.tgz
mv package NeteaseCloudMusicApi
cd NeteaseCloudMusicApi
```

2. 依存関係をインストール

```shell
npm install
```

3. 実行

```shell
node app.js
```

4. ポートのカスタマイズ

4.1 Linux

```shell
PORT=4000 node app.js
```

4.2 Windows

```bat
set PORT=4000 && node app.js
```
