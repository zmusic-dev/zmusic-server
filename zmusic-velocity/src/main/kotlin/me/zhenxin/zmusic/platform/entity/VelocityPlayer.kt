package me.zhenxin.zmusic.platform.entity

import com.velocitypowered.api.proxy.Player
import me.zhenxin.zmusic.api.entity.ZPlayer
import me.zhenxin.zmusic.utils.colored
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.title.Title
import java.time.Duration
import java.util.UUID

/**
 * Velocity 玩家实现
 *
 * @author 真心
 * @since 2024/12/9
 */
class VelocityPlayer(private val player: Player) : ZPlayer {

    override val uuid: UUID get() = player.uniqueId

    override val name: String get() = player.username

    override val isOnline: Boolean get() = player.isActive

    override fun sendMessage(message: String) {
        player.sendMessage(LegacyComponentSerializer.legacySection().deserialize(message.colored()))
    }

    override fun sendMessage(message: Component) {
        player.sendMessage(message)
    }

    override fun sendActionBar(message: Component) {
        player.sendActionBar(message)
    }

    override fun sendTitle(title: Component, subtitle: Component, fadeIn: Int, stay: Int, fadeOut: Int) {
        val times = Title.Times.times(
            Duration.ofMillis(fadeIn * 50L),
            Duration.ofMillis(stay * 50L),
            Duration.ofMillis(fadeOut * 50L)
        )
        player.showTitle(Title.title(title, subtitle, times))
    }

    override fun sendPluginMessage(channel: String, data: ByteArray) {
        player.currentServer.ifPresent { server ->
            server.sendPluginMessage({ channel }, data)
        }
    }

    override fun hasPermission(permission: String): Boolean = player.hasPermission(permission)

    @Suppress("UNCHECKED_CAST")
    override fun <T> getPlatformPlayer(): T = player as T

    @Suppress("UNCHECKED_CAST")
    override fun <T> getPlatformSender(): T = player as T

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VelocityPlayer) return false
        return uuid == other.uuid
    }

    override fun hashCode(): Int = uuid.hashCode()
}
