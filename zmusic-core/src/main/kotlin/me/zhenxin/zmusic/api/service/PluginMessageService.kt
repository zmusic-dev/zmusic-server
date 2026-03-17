package me.zhenxin.zmusic.api.service

import me.zhenxin.zmusic.api.entity.ZPlayer

/**
 * 插件消息服务接口
 *
 * @author 真心
 * @since 2024/12/9
 */
interface PluginMessageService {
    /**
     * 注册插件消息通道
     */
    fun registerChannel(channel: String)

    /**
     * 注销插件消息��道
     */
    fun unregisterChannel(channel: String)

    /**
     * 发送插件消息给玩家
     */
    fun sendPluginMessage(player: ZPlayer, channel: String, data: ByteArray)

    /**
     * 注册消息监听器
     */
    fun registerListener(channel: String, listener: PluginMessageListener)

    /**
     * 注销消息监听器
     */
    fun unregisterListener(channel: String, listener: PluginMessageListener)
}

/**
 * 插件消息监听器
 */
fun interface PluginMessageListener {
    /**
     * 收到插件消息
     *
     * @param player 发送消息的玩家
     * @param channel 通道名称
     * @param data 消息数据
     */
    fun onPluginMessageReceived(player: ZPlayer, channel: String, data: ByteArray)
}
