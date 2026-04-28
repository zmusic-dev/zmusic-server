---
title: Command Permissions
---

# Command Permissions {#command-permissions}

::: warning V4 Documentation
This version is still under development and may contain errors or incomplete features.

The current long-term support version is V2. See [V2 Docs](/en/v2/)
:::

::: info
V4 has base commands and permission control in place. Music playback, search, stop, and playlist commands can already be entered, but their features are still under development and currently report that they are unavailable.
:::

## Root command {#root-command}

The main ZMusic command is `/zmusic`, with two shorter aliases available.

| Command | Required permission | Purpose |
| --- | --- | --- |
| `/zmusic` | `zmusic.use` | Open the ZMusic root command; shows help when no arguments are provided |
| `/music` | `zmusic.use` | Alias for `/zmusic` |
| `/zm` | `zmusic.use` | Alias for `/zmusic` |

## Currently available commands {#available-commands}

### Help {#help-command}

| Command | Required permission | Purpose |
| --- | --- | --- |
| `/zmusic help` | `zmusic.help` | View the available command list |
| `/zmusic help <command>` | `zmusic.help` | View help for a specific command |

Examples:

```text
/zmusic help
/zmusic help reload
```

### Plugin information {#info-command}

| Command | Required permission | Purpose |
| --- | --- | --- |
| `/zmusic info` | `zmusic.info` | View plugin version, current platform, and online player count |

Example:

```text
/zmusic info
```

### Reload configuration {#reload-command}

| Command | Required permission | Purpose |
| --- | --- | --- |
| `/zmusic reload` | `zmusic.reload` | Reload configuration and language files |

Use this after editing configuration files. If the configuration has an error, the command reports that reload failed.

Example:

```text
/zmusic reload
```

## Unavailable commands {#unavailable-commands}

The following command entries and permission nodes are reserved, but music features are still under development. Running them currently reports that the feature is unavailable.

| Command | Required permission | Planned use |
| --- | --- | --- |
| `/zmusic play <keyword>` | `zmusic.play` | Play music by keyword |
| `/zmusic search <keyword>` | `zmusic.search` | Search music by keyword |
| `/zmusic stop` | `zmusic.stop` | Stop current playback |
| `/zmusic playlist list` | `zmusic.playlist` | View personal playlists |
| `/zmusic playlist create <name>` | `zmusic.playlist` | Create a personal playlist |
| `/zmusic playlist delete <name>` | `zmusic.playlist` | Delete a personal playlist |
| `/zmusic playlist add <playlist> <song>` | `zmusic.playlist` | Add a song to a personal playlist |
| `/zmusic playlist remove <playlist> <song>` | `zmusic.playlist` | Remove a song from a personal playlist |
| `/zmusic playlist play <playlist>` | `zmusic.playlist` | Play a personal playlist |
| `/zmusic playlist global <subcommand>` | `zmusic.admin` | Manage global playlists |

Global playlist subcommands include `list`, `create`, `delete`, `add`, `remove`, and `play`. They all require the `zmusic.admin` permission.

## Reserved permissions {#reserved-permissions}

Future volume and lyrics features will use these permissions:

| Permission | Planned use |
| --- | --- |
| `zmusic.volume` | Adjust volume |
| `zmusic.lyric` | Show lyrics |
