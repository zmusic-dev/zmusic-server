package me.zhenxin.zmusic.platform.command

import me.zhenxin.zmusic.command.ZMusicCommandManager
import me.zhenxin.zmusic.platform.entity.BungeeCommandSender
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.plugin.Command
import net.md_5.bungee.api.plugin.Plugin

class BungeeCommandRegistrar(private val plugin: Plugin) {

    fun register() {
        plugin.proxy.pluginManager.registerCommand(plugin, RootCommand())
    }

    private class RootCommand : Command(
        ZMusicCommandManager.ROOT_COMMAND,
        ZMusicCommandManager.rootPermission().node,
        "music",
        "zm"
    ) {

        override fun execute(sender: CommandSender, args: Array<out String>) {
            ZMusicCommandManager.execute(BungeeCommandSender(sender), name, args)
        }
    }
}
