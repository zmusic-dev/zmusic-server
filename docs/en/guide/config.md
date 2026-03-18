---
title: Configuration
---

# Configuration

<V4Warning />

For the TOML specification, please refer to [toml.io](https://toml.io/en/).

```toml
# Enable debug mode
debug = false
# Check for updates
check-update = true
# Plugin language
# Supported languages:
# auto: Auto detect (default)
# en-US: English
# zh-CN: Simplified Chinese
language = "auto"
# Message prefix
prefix = "&bZMusic &e>>> &r"

# API settings
[api]
# Netease Cloud Music
netease-link = "https://ncm.zhenxin.me"

# ZMusic VIP settings
[vip]
# Authorized QQ
qq = ""
# Authorization key
key = ""

# Proxy settings
[proxy]
# Enable proxy
enable = false
# Proxy type HTTP/SOCKS
type = "HTTP"
# Hostname
hostname = "127.0.0.1"
# Port
port = 8080
```
