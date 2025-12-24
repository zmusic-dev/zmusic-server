package me.zhenxin.zmusic.api.service

import me.zhenxin.zmusic.api.entity.ZCommandSender
import me.zhenxin.zmusic.api.entity.ZPlayer
import net.kyori.adventure.text.Component

/**
 * 消息服务接口
 *
 * @author 真心
 * @since 2024/12/9
 */
interface MessageService {
    /**
     * 发送消息给命令发送者
     */
    fun sendMessage(sender: ZCommandSender, message: String)

    /**
     * 发送消息给命令发送者 (Component)
     */
    fun sendMessage(sender: ZCommandSender, message: Component)

    /**
     * 发送 ActionBar 给玩家
     */
    fun sendActionBar(player: ZPlayer, message: Component)

    /**
     * 发送 Title 给玩家
     */
    fun sendTitle(
        player: ZPlayer,
        title: Component,
        subtitle: Component,
        fadeIn: Int = 10,
        stay: Int = 70,
        fadeOut: Int = 20
    )

    /**
     * 广播消息给所有玩家
     */
    fun broadcast(message: String)

    /**
     * 广播消息给所有玩家 (Component)
     */
    fun broadcast(message: Component)

    /**
     * 广播 ActionBar 给所有玩家
     */
    fun broadcastActionBar(message: Component)
}
