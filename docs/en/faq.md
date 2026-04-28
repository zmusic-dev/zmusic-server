---
title: FAQ
---

# FAQ {#faq}

::: warning
**The FAQ here only applies to the V4 version**

**For the V2 version, please refer to [FAQ (V2 Archive)](/en/v2/faq)**
:::

### Why do playback, search, stop, and playlist commands say they are unavailable? {#why-are-music-commands-unavailable}

V4 currently has base commands and permission control in place, but music features are still under development.

As a result, `/zmusic play`, `/zmusic search`, `/zmusic stop`, `/zmusic playlist`, and their subcommands currently report that the feature is unavailable.

### Which commands can I use now? {#available-commands}

Currently available commands are:

- `/zmusic help [command]`
- `/zmusic info`
- `/zmusic reload`

The root command aliases are `/music` and `/zm`.

### My server has no Internet access. Can I still use ZMusic? {#no-network}

The currently available base commands do not depend on network access to a music API.

After music playback, search, lyrics, and playlists are completed, those features will need access to the configured music API service.

### Does V4 support Netease Cloud Music login? {#netease-login}

Not yet. The current V4 version does not include `/zm login` or an equivalent login command.

### What are the Netease API docs for right now? {#netease-api-docs-purpose}

The Netease API docs describe deployable external API services. V4 music features are not connected to these services yet, so they currently serve as preparation material for upcoming feature development.
