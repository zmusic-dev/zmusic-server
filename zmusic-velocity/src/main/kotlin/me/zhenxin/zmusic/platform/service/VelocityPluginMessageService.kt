package me.zhenxin.zmusic.platform.service

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PluginMessageEvent
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier
import me.zhenxin.zmusic.api.entity.ZPlayer
import me.zhenxin.zmusic.api.service.PluginMessageListener
import me.zhenxin.zmusic.api.service.PluginMessageService
import me.zhenxin.zmusic.platform.entity.VelocityPlayer

/**
 * Velocity 插件消息服务实现
 *
 * @author 真心
 * @since 2024/12/9
 */
class VelocityPluginMessageService(
    private val server: ProxyServer,
    private val plugin: Any
) : PluginMessageService {

    private val listeners = mutableMapOf<String, MutableList<PluginMessageListener>>()
    private val channelIdentifiers = mutableMapOf<String, MinecraftChannelIdentifier>()

    init {
        server.eventManager.register(plugin, this)
    }

    override fun registerChannel(channel: String) {
        if (channel !in channelIdentifiers) {
            val identifier = MinecraftChannelIdentifier.from(channel)
            server.channelRegistrar.register(identifier)
            channelIdentifiers[channel] = identifier
        }
    }

    override fun unregisterChannel(channel: String) {
        channelIdentifiers[channel]?.let { identifier ->
            server.channelRegistrar.unregister(identifier)
            channelIdentifiers.remove(channel)
            listeners.remove(channel)
        }
    }

    override fun sendPluginMessage(player: ZPlayer, channel: String, data: ByteArray) {
        val velocityPlayer: Player = player.getPlatformPlayer()
        channelIdentifiers[channel]?.let { identifier ->
            velocityPlayer.currentServer.ifPresent { server ->
                server.sendPluginMessage(identifier, data)
            }
        }
    }

    override fun registerListener(channel: String, listener: PluginMessageListener) {
        listeners.getOrPut(channel) { mutableListOf() }.add(listener)
    }

    override fun unregisterListener(channel: String, listener: PluginMessageListener) {
        listeners[channel]?.remove(listener)
    }

    @Subscribe
    fun onPluginMessage(event: PluginMessageEvent) {
        val channel = event.identifier.id
        if (channel in channelIdentifiers && event.source is Player) {
            val player = VelocityPlayer(event.source as Player)
            listeners[channel]?.forEach { it.onPluginMessageReceived(player, channel, event.data) }
        }
    }
}
