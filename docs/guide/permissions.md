---
title: 权限
---

# 权限 {#permissions}

::: warning V4 版本文档
此版本仍在开发中，可能存在错误或不完善的地方。

当前长期支持版本为 V2，您可以查看 [V2 文档](/v2/)
:::

::: info
V4R 权限节点已按功能拆分。部分命令仍在开发中，权限节点会用于后续命令功能接入。
:::

## 普通玩家权限 {#player-permissions}

| 权限节点 | 说明 | 默认值 |
| --- | --- | --- |
| `zmusic.use` | 使用 ZMusic 根命令 | true |
| `zmusic.help` | 查看帮助 | true |
| `zmusic.info` | 查看插件状态 | true |
| `zmusic.play` | 播放音乐 | true |
| `zmusic.search` | 搜索音乐 | true |
| `zmusic.stop` | 停止播放 | true |
| `zmusic.volume` | 调整音量 | true |
| `zmusic.lyric` | 显示歌词 | true |
| `zmusic.playlist` | 管理歌单 | true |

## 管理员权限 {#admin-permissions}

| 权限节点 | 说明 | 默认值 |
| --- | --- | --- |
| `zmusic.reload` | 重载配置 | op |
| `zmusic.admin` | 使用所有管理命令 | op |

## 平台说明 {#platform-notes}

- Bukkit 在 `plugin.yml` 中声明默认权限。
- BungeeCord 和 Velocity 通过平台原生权限系统检查 `hasPermission`，不在插件元数据中声明默认权限。
