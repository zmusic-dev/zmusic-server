---
title: 命令权限
---

# 命令权限 {#command-permissions}

::: warning V4 版本文档
此版本仍在开发中，可能存在错误或不完善的地方。

当前长期支持版本为 V2，您可以查看 [V2 文档](/v2/)
:::

::: info
V4 已完成基础命令和权限控制。音乐播放、搜索、停止和歌单相关命令已经可以输入，但具体功能仍在开发中，当前会提示功能暂不可用。
:::

## 根命令 {#root-command}

ZMusic 的主命令是 `/zmusic`，同时提供两个简短别名。

| 命令 | 需要权限 | 用途 |
| --- | --- | --- |
| `/zmusic` | `zmusic.use` | 打开 ZMusic 根命令，未带参数时显示帮助 |
| `/music` | `zmusic.use` | `/zmusic` 的别名 |
| `/zm` | `zmusic.use` | `/zmusic` 的别名 |

## 当前可用命令 {#available-commands}

### 查看帮助 {#help-command}

| 命令 | 需要权限 | 用途 |
| --- | --- | --- |
| `/zmusic help` | `zmusic.help` | 查看可用命令列表 |
| `/zmusic help <command>` | `zmusic.help` | 查看指定命令的帮助 |

示例：

```text
/zmusic help
/zmusic help reload
```

### 查看插件信息 {#info-command}

| 命令 | 需要权限 | 用途 |
| --- | --- | --- |
| `/zmusic info` | `zmusic.info` | 查看插件版本、当前平台和在线人数 |

示例：

```text
/zmusic info
```

### 重载配置 {#reload-command}

| 命令 | 需要权限 | 用途 |
| --- | --- | --- |
| `/zmusic reload` | `zmusic.reload` | 重新加载配置文件和语言文件 |

适合在修改配置后使用。若配置文件存在错误，命令会提示重载失败。

示例：

```text
/zmusic reload
```

## 暂不可用命令 {#unavailable-commands}

以下命令已占用命令入口和权限节点，但音乐功能仍在开发中，当前执行时会提示功能暂不可用。

| 命令 | 需要权限 | 计划用途 |
| --- | --- | --- |
| `/zmusic play <keyword>` | `zmusic.play` | 按关键词播放音乐 |
| `/zmusic search <keyword>` | `zmusic.search` | 按关键词搜索音乐 |
| `/zmusic stop` | `zmusic.stop` | 停止当前播放 |
| `/zmusic playlist list` | `zmusic.playlist` | 查看个人歌单 |
| `/zmusic playlist create <name>` | `zmusic.playlist` | 创建个人歌单 |
| `/zmusic playlist delete <name>` | `zmusic.playlist` | 删除个人歌单 |
| `/zmusic playlist add <playlist> <song>` | `zmusic.playlist` | 向个人歌单添加歌曲 |
| `/zmusic playlist remove <playlist> <song>` | `zmusic.playlist` | 从个人歌单移除歌曲 |
| `/zmusic playlist play <playlist>` | `zmusic.playlist` | 播放个人歌单 |
| `/zmusic playlist global <subcommand>` | `zmusic.admin` | 管理全局歌单 |

全局歌单子命令包括 `list`、`create`、`delete`、`add`、`remove`、`play`，均需要 `zmusic.admin` 权限。

## 预留权限 {#reserved-permissions}

后续音量和歌词功能会分别使用以下权限：

| 权限 | 计划用途 |
| --- | --- |
| `zmusic.volume` | 调整音量 |
| `zmusic.lyric` | 显示歌词 |
