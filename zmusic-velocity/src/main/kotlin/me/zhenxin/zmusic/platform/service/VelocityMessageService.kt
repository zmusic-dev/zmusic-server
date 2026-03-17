package me.zhenxin.zmusic.platform.service

import com.velocitypowered.api.proxy.ProxyServer
import me.zhenxin.zmusic.api.entity.ZCommandSender
import me.zhenxin.zmusic.api.entity.ZPlayer
import me.zhenxin.zmusic.api.service.MessageService
import me.zhenxin.zmusic.utils.colored
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

/**
 * Velocity 消息服务实现
 *
 * @author 真心
 * @since 2024/12/9
 */
class VelocityMessageService(private val server: ProxyServer) : MessageService {

    private val serializer = LegacyComponentSerializer.legacySection()

    override fun sendMessage(sender: ZCommandSender, message: String) {
        sender.sendMessage(message.colored())
    }

    override fun sendMessage(sender: ZCommandSender, message: Component) {
        sender.sendMessage(message)
    }

    override fun sendActionBar(player: ZPlayer, message: Component) {
        player.sendActionBar(message)
    }

    override fun sendTitle(player: ZPlayer, title: Component, subtitle: Component, fadeIn: Int, stay: Int, fadeOut: Int) {
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut)
    }

    override fun broadcast(message: String) {
        val component = serializer.deserialize(message.colored())
        server.allPlayers.forEach { it.sendMessage(component) }
    }

    override fun broadcast(message: Component) {
        server.allPlayers.forEach { it.sendMessage(message) }
    }

    override fun broadcastActionBar(message: Component) {
        server.allPlayers.forEach { it.sendActionBar(message) }
    }
}
