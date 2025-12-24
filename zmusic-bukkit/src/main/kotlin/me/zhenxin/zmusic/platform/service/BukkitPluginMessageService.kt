package me.zhenxin.zmusic.platform.service

import me.zhenxin.zmusic.api.entity.ZPlayer
import me.zhenxin.zmusic.api.service.PluginMessageListener
import me.zhenxin.zmusic.api.service.PluginMessageService
import me.zhenxin.zmusic.platform.entity.BukkitPlayer
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.messaging.PluginMessageListener as BukkitPluginMessageListener

/**
 * Bukkit 插件消息服务实现
 *
 * @author 真心
 * @since 2024/12/9
 */
class BukkitPluginMessageService(private val plugin: Plugin) : PluginMessageService {

    private val listeners = mutableMapOf<String, MutableList<PluginMessageListener>>()
    private val bukkitListeners = mutableMapOf<String, BukkitPluginMessageListener>()

    override fun registerChannel(channel: String) {
        plugin.server.messenger.registerOutgoingPluginChannel(plugin, channel)

        val bukkitListener = BukkitPluginMessageListener { _, player, data ->
            if (player is Player) {
                val zPlayer = BukkitPlayer(player)
                listeners[channel]?.forEach { it.onPluginMessageReceived(zPlayer, channel, data) }
            }
        }
        bukkitListeners[channel] = bukkitListener
        plugin.server.messenger.registerIncomingPluginChannel(plugin, channel, bukkitListener)
    }

    override fun unregisterChannel(channel: String) {
        plugin.server.messenger.unregisterOutgoingPluginChannel(plugin, channel)

        bukkitListeners[channel]?.let {
            plugin.server.messenger.unregisterIncomingPluginChannel(plugin, channel, it)
        }
        bukkitListeners.remove(channel)
        listeners.remove(channel)
    }

    override fun sendPluginMessage(player: ZPlayer, channel: String, data: ByteArray) {
        val bukkitPlayer: Player = player.getPlatformPlayer()
        bukkitPlayer.sendPluginMessage(plugin, channel, data)
    }

    override fun registerListener(channel: String, listener: PluginMessageListener) {
        listeners.getOrPut(channel) { mutableListOf() }.add(listener)
    }

    override fun unregisterListener(channel: String, listener: PluginMessageListener) {
        listeners[channel]?.remove(listener)
    }
}
