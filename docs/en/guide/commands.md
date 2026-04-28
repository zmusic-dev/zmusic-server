---
title: Commands
---

# Commands {#commands}

::: warning V4 Documentation
This version is still under development and may contain errors or incomplete features.

The current long-term support version is V2. See [V2 Docs](/en/v2/)
:::

::: info
The V4R command system has command registration and permission checks wired. Music playback, search, stop, and playlist commands already have reserved entry points, but the music backend is still under development and currently reports that the feature is unavailable.
:::

## Root command {#root-command}

| Command | Description |
| --- | --- |
| `/zmusic` | ZMusic root command |
| `/music` | Alias for `/zmusic` |
| `/zm` | Alias for `/zmusic` |

## Available commands {#available-commands}

| Command | Permission | Description |
| --- | --- | --- |
| `/zmusic help [command]` | `zmusic.help` | View command help |
| `/zmusic info` | `zmusic.info` | View plugin version, platform, and online player count |
| `/zmusic reload` | `zmusic.reload` | Reload configuration and language files |

## Registered but unavailable commands {#reserved-commands}

| Command | Permission | Planned use |
| --- | --- | --- |
| `/zmusic play <keyword>` | `zmusic.play` | Play music |
| `/zmusic search <keyword>` | `zmusic.search` | Search music |
| `/zmusic stop` | `zmusic.stop` | Stop playback |
| `/zmusic playlist <list\|create\|delete\|add\|remove\|play\|global>` | `zmusic.playlist` | Manage personal or global playlists |

`playlist global` and its subcommands require the `zmusic.admin` permission.
