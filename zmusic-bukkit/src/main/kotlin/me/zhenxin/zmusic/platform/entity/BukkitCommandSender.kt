package me.zhenxin.zmusic.platform.entity

import me.zhenxin.zmusic.api.entity.ZCommandSender
import me.zhenxin.zmusic.utils.colored
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

/**
 * Bukkit 命令发送者实现
 *
 * @author 真心
 * @since 2024/12/9
 */
class BukkitCommandSender(private val sender: CommandSender) : ZCommandSender {

    override val name: String get() = sender.name

    override val isConsole: Boolean get() = sender is ConsoleCommandSender

    override val isPlayer: Boolean get() = sender is Player

    override fun sendMessage(message: String) {
        sender.sendMessage(message.colored())
    }

    override fun sendMessage(message: Component) {
        val legacy = LegacyComponentSerializer.legacySection().serialize(message)
        sender.sendMessage(legacy)
    }

    override fun hasPermission(permission: String): Boolean = sender.hasPermission(permission)

    @Suppress("UNCHECKED_CAST")
    override fun <T> getPlatformSender(): T = sender as T

    override fun asPlayer(): BukkitPlayer? {
        return if (sender is Player) BukkitPlayer(sender) else null
    }
}
