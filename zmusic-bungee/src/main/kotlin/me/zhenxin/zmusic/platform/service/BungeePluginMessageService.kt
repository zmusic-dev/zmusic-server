package me.zhenxin.zmusic.platform.service

import me.zhenxin.zmusic.api.entity.ZPlayer
import me.zhenxin.zmusic.api.service.PluginMessageListener
import me.zhenxin.zmusic.api.service.PluginMessageService
import me.zhenxin.zmusic.platform.entity.BungeePlayer
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.connection.Server
import net.md_5.bungee.api.event.PluginMessageEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.event.EventHandler

/**
 * BungeeCord 插件消息服务实现
 *
 * @author 真心
 * @since 2024/12/9
 */
class BungeePluginMessageService(private val plugin: Plugin) : PluginMessageService, Listener {

    private val listeners = mutableMapOf<String, MutableList<PluginMessageListener>>()
    private val registeredChannels = mutableSetOf<String>()

    init {
        ProxyServer.getInstance().pluginManager.registerListener(plugin, this)
    }

    override fun registerChannel(channel: String) {
        if (channel !in registeredChannels) {
            ProxyServer.getInstance().registerChannel(channel)
            registeredChannels.add(channel)
        }
    }

    override fun unregisterChannel(channel: String) {
        if (channel in registeredChannels) {
            ProxyServer.getInstance().unregisterChannel(channel)
            registeredChannels.remove(channel)
            listeners.remove(channel)
        }
    }

    override fun sendPluginMessage(player: ZPlayer, channel: String, data: ByteArray) {
        val proxiedPlayer: ProxiedPlayer = player.getPlatformPlayer()
        proxiedPlayer.server?.sendData(channel, data)
    }

    override fun registerListener(channel: String, listener: PluginMessageListener) {
        listeners.getOrPut(channel) { mutableListOf() }.add(listener)
    }

    override fun unregisterListener(channel: String, listener: PluginMessageListener) {
        listeners[channel]?.remove(listener)
    }

    @EventHandler
    fun onPluginMessage(event: PluginMessageEvent) {
        val channel = event.tag
        if (channel in registeredChannels) {
            val player = resolvePlayer(event) ?: return
            listeners[channel]?.forEach { it.onPluginMessageReceived(player, channel, event.data) }
        }
    }

    private fun resolvePlayer(event: PluginMessageEvent): BungeePlayer? {
        val sender = event.sender
        if (sender is ProxiedPlayer) {
            return BungeePlayer(sender)
        }

        val receiver = event.receiver
        if (receiver is ProxiedPlayer) {
            return BungeePlayer(receiver)
        }

        if (sender is Server) {
            return ProxyServer.getInstance().players
                .firstOrNull { it.server == sender }
                ?.let(::BungeePlayer)
        }

        return null
    }
}
