package me.zhenxin.zmusic.platform.service

import me.zhenxin.zmusic.api.entity.ZCommandSender
import me.zhenxin.zmusic.api.entity.ZPlayer
import me.zhenxin.zmusic.api.service.MessageService
import me.zhenxin.zmusic.utils.colored
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit

/**
 * Bukkit 消息服务实现
 *
 * @author 真心
 * @since 2024/12/9
 */
@Suppress("DEPRECATION")
class BukkitMessageService : MessageService {

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
        Bukkit.broadcastMessage(message.colored())
    }

    override fun broadcast(message: Component) {
        val legacy = serializer.serialize(message)
        Bukkit.broadcastMessage(legacy)
    }

    override fun broadcastActionBar(message: Component) {
        val legacy = serializer.serialize(message)
        Bukkit.getOnlinePlayers().forEach {
            it.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(legacy))
        }
    }
}
