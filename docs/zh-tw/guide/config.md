---
title: 設定檔
---

# 設定檔

<V4Warning />

TOML 規範說明請參考 [toml.io](https://toml.io/cn/)

```toml
# 是否開啟除錯模式
debug = false
# 是否檢查更新
check-update = true
# 插件使用的語言
# 支援的語言:
# auto: 自動檢測 (預設)
# en-US: English
# zh-CN: 簡體中文
language = "auto"
# 訊息前綴
prefix = "&bZMusic &e>>> &r"

# API設定
[api]
# 網易雲音樂
netease-link = "https://ncm.zhenxin.me"

# ZMusic VIP設定
[vip]
# 授權QQ
qq = ""
# 授權Key
key = ""

# 代理設定
[proxy]
# 是否啟用代理
enable = false
# 代理類型 HTTP/SOCKS
type = "HTTP"
# 主機名
hostname = "127.0.0.1"
# 連接埠
port = 8080
```
