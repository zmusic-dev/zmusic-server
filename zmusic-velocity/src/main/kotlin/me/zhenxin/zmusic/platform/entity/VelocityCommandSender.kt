package me.zhenxin.zmusic.platform.entity

import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.ConsoleCommandSource
import com.velocitypowered.api.proxy.Player
import me.zhenxin.zmusic.api.entity.ZCommandSender
import me.zhenxin.zmusic.utils.colored
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

/**
 * Velocity 命令发送者实现
 *
 * @author 真心
 * @since 2024/12/9
 */
class VelocityCommandSender(private val sender: CommandSource) : ZCommandSender {

    override val name: String
        get() = when (sender) {
            is Player -> sender.username
            is ConsoleCommandSource -> "Console"
            else -> "Unknown"
        }

    override val isConsole: Boolean get() = sender is ConsoleCommandSource

    override val isPlayer: Boolean get() = sender is Player

    override fun sendMessage(message: String) {
        sender.sendMessage(LegacyComponentSerializer.legacySection().deserialize(message.colored()))
    }

    override fun sendMessage(message: Component) {
        sender.sendMessage(message)
    }

    override fun hasPermission(permission: String): Boolean = sender.hasPermission(permission)

    @Suppress("UNCHECKED_CAST")
    override fun <T> getPlatformSender(): T = sender as T

    override fun asPlayer(): VelocityPlayer? {
        return if (sender is Player) VelocityPlayer(sender) else null
    }
}
