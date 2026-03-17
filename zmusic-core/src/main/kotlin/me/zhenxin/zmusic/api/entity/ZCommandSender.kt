package me.zhenxin.zmusic.api.entity

import net.kyori.adventure.text.Component
import java.util.UUID

/**
 * 命令发送者抽象
 *
 * @author 真心
 * @since 2024/12/9
 */
interface ZCommandSender {
    /**
     * 发送者名称
     */
    val name: String

    /**
     * 是否是控制台
     */
    val isConsole: Boolean

    /**
     * 是否是玩家
     */
    val isPlayer: Boolean

    /**
     * 发送消息
     */
    fun sendMessage(message: String)

    /**
     * 发送消息 (Adventure Component)
     */
    fun sendMessage(message: Component)

    /**
     * 检查权限
     */
    fun hasPermission(permission: String): Boolean

    /**
     * 转换为玩家（如果是玩家的话）
     */
    fun asPlayer(): ZPlayer? = if (isPlayer) this as? ZPlayer else null

    /**
     * 获取原始平台对象
     */
    fun <T> getPlatformSender(): T
}

/**
 * 玩家抽象
 *
 * @author 真心
 * @since 2024/12/9
 */
interface ZPlayer : ZCommandSender {
    /**
     * 玩家 UUID
     */
    val uuid: UUID

    /**
     * 玩家名称
     */
    override val name: String

    /**
     * 是否在线
     */
    val isOnline: Boolean

    override val isConsole: Boolean get() = false
    override val isPlayer: Boolean get() = true

    /**
     * 发送 ActionBar
     */
    fun sendActionBar(message: Component)

    /**
     * 发送 Title
     */
    fun sendTitle(title: Component, subtitle: Component, fadeIn: Int, stay: Int, fadeOut: Int)

    /**
     * 发送插件消息
     */
    fun sendPluginMessage(channel: String, data: ByteArray)

    /**
     * 获取原始平台玩家对象
     */
    fun <T> getPlatformPlayer(): T
}
