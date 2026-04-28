---
title: Permissions
---

# Permissions {#permissions}

::: warning V4 Documentation
This version is still under development and may contain errors or incomplete features.

The current long-term support version is V2. See [V2 Docs](/en/v2/)
:::

::: info
V4R permission nodes are split by feature. Some commands are still under development, and these nodes will be used as the command features are connected.
:::

## Regular player permissions {#player-permissions}

| Permission node | Description | Default |
| --- | --- | --- |
| `zmusic.use` | Use the ZMusic root command | true |
| `zmusic.help` | View help | true |
| `zmusic.info` | View plugin status | true |
| `zmusic.play` | Play music | true |
| `zmusic.search` | Search music | true |
| `zmusic.stop` | Stop playback | true |
| `zmusic.volume` | Adjust volume | true |
| `zmusic.lyric` | Show lyrics | true |
| `zmusic.playlist` | Manage playlists | true |

## Administrator permissions {#admin-permissions}

| Permission node | Description | Default |
| --- | --- | --- |
| `zmusic.reload` | Reload configuration | op |
| `zmusic.admin` | Use all administrative commands | op |

## Platform notes {#platform-notes}

- Bukkit declares default permissions in `plugin.yml`.
- BungeeCord and Velocity check `hasPermission` through the native platform permission system and do not declare default permissions in plugin metadata.
