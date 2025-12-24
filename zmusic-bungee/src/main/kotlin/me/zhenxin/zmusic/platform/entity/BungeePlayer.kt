package me.zhenxin.zmusic.platform.entity

import me.zhenxin.zmusic.api.entity.ZPlayer
import me.zhenxin.zmusic.utils.colored
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.connection.ProxiedPlayer
import java.util.UUID

/**
 * BungeeCord 玩家实现
 *
 * @author 真心
 * @since 2024/12/9
 */
@Suppress("DEPRECATION")
class BungeePlayer(private val player: ProxiedPlayer) : ZPlayer {

    override val uuid: UUID get() = player.uniqueId

    override val name: String get() = player.name

    override val isOnline: Boolean get() = player.isConnected

    override fun sendMessage(message: String) {
        player.sendMessage(TextComponent(message.colored()))
    }

    override fun sendMessage(message: Component) {
        val legacy = LegacyComponentSerializer.legacySection().serialize(message)
        player.sendMessage(TextComponent(legacy))
    }

    override fun sendActionBar(message: Component) {
        val legacy = LegacyComponentSerializer.legacySection().serialize(message)
        player.sendMessage(ChatMessageType.ACTION_BAR, TextComponent(legacy))
    }

    override fun sendTitle(title: Component, subtitle: Component, fadeIn: Int, stay: Int, fadeOut: Int) {
        val titleLegacy = LegacyComponentSerializer.legacySection().serialize(title)
        val subtitleLegacy = LegacyComponentSerializer.legacySection().serialize(subtitle)

        val titleObj = player.server?.info?.let {
            net.md_5.bungee.api.ProxyServer.getInstance().createTitle()
                .title(TextComponent(titleLegacy))
                .subTitle(TextComponent(subtitleLegacy))
                .fadeIn(fadeIn)
                .stay(stay)
                .fadeOut(fadeOut)
        }
        titleObj?.send(player)
    }

    override fun sendPluginMessage(channel: String, data: ByteArray) {
        player.server?.sendData(channel, data)
    }

    override fun hasPermission(permission: String): Boolean = player.hasPermission(permission)

    @Suppress("UNCHECKED_CAST")
    override fun <T> getPlatformPlayer(): T = player as T

    @Suppress("UNCHECKED_CAST")
    override fun <T> getPlatformSender(): T = player as T

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BungeePlayer) return false
        return uuid == other.uuid
    }

    override fun hashCode(): Int = uuid.hashCode()
}
