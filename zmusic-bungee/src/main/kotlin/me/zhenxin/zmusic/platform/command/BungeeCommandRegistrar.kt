package me.zhenxin.zmusic.platform.command

import me.zhenxin.zmusic.command.ZMusicCommandManager
import me.zhenxin.zmusic.platform.entity.BungeeCommandSender
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.plugin.Command
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.api.plugin.TabExecutor

class BungeeCommandRegistrar(private val plugin: Plugin) {

    fun register() {
        plugin.proxy.pluginManager.registerCommand(plugin, RootCommand())
    }

    private class RootCommand : Command(
        ZMusicCommandManager.ROOT_COMMAND,
        null,
        "music",
        "zm"
    ), TabExecutor {

        override fun execute(sender: CommandSender, args: Array<out String>) {
            ZMusicCommandManager.execute(BungeeCommandSender(sender), name, args)
        }

        override fun onTabComplete(sender: CommandSender, args: Array<out String>): Iterable<String> {
            return ZMusicCommandManager.suggest(BungeeCommandSender(sender), name, args)
        }
    }
}
