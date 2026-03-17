package me.zhenxin.zmusic.platform.entity

import me.zhenxin.zmusic.api.entity.ZPlayer
import me.zhenxin.zmusic.utils.colored
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.entity.Player
import java.util.UUID

/**
 * Bukkit 玩家实现
 *
 * @author 真心
 * @since 2024/12/9
 */
@Suppress("DEPRECATION")
class BukkitPlayer(private val player: Player) : ZPlayer {

    override val uuid: UUID get() = player.uniqueId

    override val name: String get() = player.name

    override val isOnline: Boolean get() = player.isOnline

    override fun sendMessage(message: String) {
        player.sendMessage(message.colored())
    }

    override fun sendMessage(message: Component) {
        val legacy = LegacyComponentSerializer.legacySection().serialize(message)
        player.sendMessage(legacy)
    }

    override fun sendActionBar(message: Component) {
        val legacy = LegacyComponentSerializer.legacySection().serialize(message)
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(legacy))
    }

    override fun sendTitle(title: Component, subtitle: Component, fadeIn: Int, stay: Int, fadeOut: Int) {
        val titleLegacy = LegacyComponentSerializer.legacySection().serialize(title)
        val subtitleLegacy = LegacyComponentSerializer.legacySection().serialize(subtitle)
        player.sendTitle(titleLegacy, subtitleLegacy, fadeIn, stay, fadeOut)
    }

    override fun sendPluginMessage(channel: String, data: ByteArray) {
        player.sendPluginMessage(player.server.pluginManager.plugins.first { it.name == "ZMusic" }, channel, data)
    }

    override fun hasPermission(permission: String): Boolean = player.hasPermission(permission)

    @Suppress("UNCHECKED_CAST")
    override fun <T> getPlatformPlayer(): T = player as T

    @Suppress("UNCHECKED_CAST")
    override fun <T> getPlatformSender(): T = player as T

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BukkitPlayer) return false
        return uuid == other.uuid
    }

    override fun hashCode(): Int = uuid.hashCode()
}
