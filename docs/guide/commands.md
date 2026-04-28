---
title: 命令
---

# 命令 {#commands}

::: warning V4 版本文档
此版本仍在开发中，可能存在错误或不完善的地方。

当前长期支持版本为 V2，您可以查看 [V2 文档](/v2/)
:::

::: info
V4R 命令系统已完成注册和权限检查。音乐播放、搜索、停止和歌单相关命令已预留入口，但业务后端仍在开发中，当前会提示功能暂不可用。
:::

## 根命令 {#root-command}

| 命令 | 说明 |
| --- | --- |
| `/zmusic` | ZMusic 根命令 |
| `/music` | `/zmusic` 的别名 |
| `/zm` | `/zmusic` 的别名 |

## 可用命令 {#available-commands}

| 命令 | 权限 | 说明 |
| --- | --- | --- |
| `/zmusic help [command]` | `zmusic.help` | 查看命令帮助 |
| `/zmusic info` | `zmusic.info` | 查看插件版本、平台和在线人数 |
| `/zmusic reload` | `zmusic.reload` | 重载配置和语言文件 |

## 已注册但暂不可用的命令 {#reserved-commands}

| 命令 | 权限 | 计划用途 |
| --- | --- | --- |
| `/zmusic play <keyword>` | `zmusic.play` | 播放音乐 |
| `/zmusic search <keyword>` | `zmusic.search` | 搜索音乐 |
| `/zmusic stop` | `zmusic.stop` | 停止播放 |
| `/zmusic playlist <list\|create\|delete\|add\|remove\|play\|global>` | `zmusic.playlist` | 管理个人或全局歌单 |

`playlist global` 及其子命令需要 `zmusic.admin` 权限。
