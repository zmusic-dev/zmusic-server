package me.zhenxin.zmusic.platform.entity

import me.zhenxin.zmusic.api.entity.ZCommandSender
import me.zhenxin.zmusic.utils.colored
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.connection.ProxiedPlayer

/**
 * BungeeCord 命令发送者实现
 *
 * @author 真心
 * @since 2024/12/9
 */
@Suppress("DEPRECATION")
class BungeeCommandSender(private val sender: CommandSender) : ZCommandSender {

    override val name: String get() = sender.name

    override val isConsole: Boolean get() = sender !is ProxiedPlayer

    override val isPlayer: Boolean get() = sender is ProxiedPlayer

    override fun sendMessage(message: String) {
        sender.sendMessage(TextComponent(message.colored()))
    }

    override fun sendMessage(message: Component) {
        val legacy = LegacyComponentSerializer.legacySection().serialize(message)
        sender.sendMessage(TextComponent(legacy))
    }

    override fun hasPermission(permission: String): Boolean = sender.hasPermission(permission)

    @Suppress("UNCHECKED_CAST")
    override fun <T> getPlatformSender(): T = sender as T

    override fun asPlayer(): BungeePlayer? {
        return if (sender is ProxiedPlayer) BungeePlayer(sender) else null
    }
}
