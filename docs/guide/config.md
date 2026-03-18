---
title: 配置文件
---

# 配置文件

<V4Warning />

TOML 规范说明请参考 [toml.io](https://toml.io/cn/)

```toml
# 是否开启调试模式
debug = false
# 是否检查更新
check-update = true
# 插件使用的语言
# 支持的语言:
# auto: 自动检测 (默认)
# en-US: English
# zh-CN: 简体中文
language = "auto"
# 消息前缀
prefix = "&bZMusic &e>>> &r"

# API设置
[api]
# 网易云音乐
netease-link = "https://ncm.zhenxin.me"

# ZMusic VIP设置
[vip]
# 授权QQ
qq = ""
# 授权Key
key = ""

# 代理设置
[proxy]
# 是否启用代理
enable = false
# 代理类型 HTTP/SOCKS
type = "HTTP"
# 主机名
hostname = "127.0.0.1"
# 端口
port = 8080
```
