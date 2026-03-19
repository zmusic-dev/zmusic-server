---
title: FAQ (V2)
---

# FAQ (V2)

Note: This FAQ only applies to the V2 version. For V4, please check the [FAQ](/en/faq).

## There is no sound when playing music

If you are using `KCauldron`, we are sorry, but this server is not supported.

If you still want to run ZMusic on a modded server, there are two common approaches:

- Use a proxy server such as [BungeeCord](https://www.spigotmc.org/wiki/bungeecord) or [WaterFall](https://papermc.io/downloads#Waterfall).
- Use a modded server core such as Arclight, LoliServer, Mohist, or Uranium, which **may** work with this plugin.

If you are using a **Spigot, Paper, Yatopia, Sugarcane, or other vanilla-style plugin server**, please make sure the following conditions are met:

- Your client has installed the ZMusic Mod (1.7.10, 1.12, or later) or the AudioBuffer Mod (1.12 and below)
- Your server has installed the ZMusic plugin correctly
- You installed a ZMusic plugin on the server, not a ZMusic mod (mods must be installed on the client)
- Your client is running Fabric or Forge, **not** a pure vanilla client, and has AudioBuffer **or** the ZMusic mod installed
- Your network connection is stable
- The music source you are using is Netease Cloud Music

If any of the conditions above are not met, please adjust your environment first.

If all conditions are met and music still cannot be played, we recommend testing with **Netease Cloud Music** without configuring an external API.

In practice, we also recommend hosting the server in an environment you control.

## After the server starts, `/zm` commands return “Error: Please wait for the plugin to finish loading”

If you encounter this issue, update ZMusic to the latest version.

## The plugin cannot load and asks you to uninstall AllMusic/AudioBuffer

Delete the AllMusic/AudioBuffer plugin from your server `plugins` folder.<br>
In newer versions of ZMusic, BossBarAPI, ActionBarAPI, and AudioBuffer are no longer used. This avoids communication channel conflicts with server mods.

## The client reports a very long elapsed time after playback succeeds, but the actual time is short

This usually happens when the same song request button is clicked repeatedly within a short interval. Internal threads have not fully finished processing, so an incorrect elapsed time is displayed. Actual playback is not affected.

## `java.net.UnknownHostException` appears when logging in to Netease Cloud Music

This usually means the external API domain configured in your file is incorrect.<br>
If the domain is valid, check whether your server DNS is functioning properly or flush the DNS cache.<br>
This rarely happens on panel-hosted servers.

## `java.lang.NoClassDefFoundError com/google/gson/xxx` appears while loading the plugin

Your server does not include the `gson` library. Install a plugin that bundles `gson`.

## Yellow warnings keep spamming the console after a song request succeeds

Since version 2.5, the old progress callback solution has been deprecated. Please update to the latest release.

## The client cannot start after installing ZMusic (Fabric)

- Make sure you downloaded the correct mod variant. For example, if your client uses Fabric but you downloaded `zmusic-forge-X.X.X-X.X.X.jar`, it will not work.<br>
- If you use the Fabric version, verify that `Fabric-API` is installed.<br>
- This mod is incompatible with `CardBoard`.

## The plugin reports “Error: Please wait for the plugin to finish loading” after startup

Please check the following:

- Confirm that you are using the latest plugin version.
- Confirm that the `ZMusic` plugin directory is writable. On Linux-based systems, avoid unsafe shortcuts like `chmod 777`.
- Confirm that your server supports asynchronous threads. Most `Bukkit`-based servers do.
- Repair the plugin folder manually: create a `language` directory inside `ZMusic`, then download `zh_cn.json` from the `language` directory on the `master` branch of our repository, place it there, and reload or restart the server.

## Is `1.7.10` supported?

We do not recommend using `1.7.10`. If you insist on it, keep the following in mind:

- Only `Mohist/Uranium` server cores are supported
- `Uranium` was only verified on `dev-4-b210`
- `Mohist` was only verified on `1.7.10-42`
- All lyric display methods except chat are unavailable (except on Uranium)
- With the matching Uranium mod, Title/ActionBar display can work
- We do not accept issue reports for `1.7.10`

## Does the plugin cause lag?

<font size="25">No.</font>
If lag occurs, it is usually caused by another plugin on the server that happens to spike while someone requests a song.<br>
Rumor says requesting the Great Compassion Mantra can crash a server.<br>
![](/images/dabeizhou_1.png)<br>
![](/images/dabeizhou_2.png)<br>
If you are certain that the crash above happened after requesting music, ask the server owner to send us the red stacktrace shown before the crash so we can investigate quickly.<br>
For safety, if you run a proxy network, we recommend placing ZMusic in the proxy plugin directory.

## DeluxeMenu/TrMenu and similar menu plugins cannot execute ZMusic commands (BungeeCord/WaterFall)

This is a common limitation on proxy networks. ZMusic commands are registered on the proxy core, while menu plugins can only invoke commands on the Spigot/Paper side.<br>
The practical workaround is to let users type the command themselves.

## [ZMusic keeps saying there is no `zmusic.use` permission even though the permission group was already configured](https://www.mcbbs.net/forum.php?mod=viewthread&tid=1310665)

If you are using BungeeCord/Waterfall, install LuckPerms-Bungee on the proxy side and grant permissions through `/lpb`.

## [[Sponge] ZMusic cannot load online music on the server side](https://www.mcbbs.net/forum.php?mod=viewthread&tid=1048579)

This plugin does not support Sponge. Please wait for version 3.0. The current 3.0 test build only has partial Sponge support.

## I cannot find my problem in the list above

Join our community channel. If you have an error message, upload the console output to [mclo.gs](https://mclo.gs)<br>
Then send us the link so we can help.<br>
The documentation is still being improved.
