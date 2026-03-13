package me.zhenxin.zmusic.platform.command

import me.zhenxin.zmusic.command.ZMusicCommandManager
import me.zhenxin.zmusic.platform.entity.BukkitCommandSender
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.plugin.java.JavaPlugin

class BukkitCommandRegistrar(private val plugin: JavaPlugin) : CommandExecutor, TabCompleter {

    fun register() {
        val command = requireNotNull(plugin.getCommand(ZMusicCommandManager.ROOT_COMMAND)) {
            "Command '${ZMusicCommandManager.ROOT_COMMAND}' is not declared in plugin.yml"
        }
        command.setExecutor(this)
        command.tabCompleter = this
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        return ZMusicCommandManager.execute(BukkitCommandSender(sender), label, args)
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String> {
        return ZMusicCommandManager.suggest(BukkitCommandSender(sender), alias, args)
    }
}
