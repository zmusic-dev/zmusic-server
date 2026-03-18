---
title: 設定ファイル
---

# 設定ファイル

<V4Warning />

TOML 仕様については [toml.io](https://toml.io/cn/) をご参照ください。

```toml
# デバッグモードを有効にするかどうか
debug = false
# アップデートを確認するかどうか
check-update = true
# プラグインで使用する言語
# サポートされている言語:
# auto: 自動検出 (デフォルト)
# en-US: English
# zh-CN: 簡体字中国語
language = "auto"
# メッセージプレフィックス
prefix = "&bZMusic &e>>> &r"

# API設定
[api]
# NetEase Cloud Music
netease-link = "https://ncm.zhenxin.me"

# ZMusic VIP設定
[vip]
# 認証QQ
qq = ""
# 認証Key
key = ""

# プロキシ設定
[proxy]
# プロキシを有効にするかどうか
enable = false
# プロキシタイプ HTTP/SOCKS
type = "HTTP"
# ホスト名
hostname = "127.0.0.1"
# ポート
port = 8080
```
