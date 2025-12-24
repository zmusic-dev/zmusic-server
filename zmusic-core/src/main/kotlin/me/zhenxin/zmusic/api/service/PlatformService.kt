package me.zhenxin.zmusic.api.service

import me.zhenxin.zmusic.enums.Platform

/**
 * 平台服务聚合接口
 * 提供所有平台相关服务的统一访问点
 *
 * @author 真心
 * @since 2024/12/9
 */
interface PlatformService {
    /**
     * 当前平台
     */
    val platform: Platform

    /**
     * 消息服务
     */
    val messageService: MessageService

    /**
     * 玩家服务
     */
    val playerService: PlayerService

    /**
     * 调度服务
     */
    val schedulerService: SchedulerService

    /**
     * 插件消息服务
     */
    val pluginMessageService: PluginMessageService

    /**
     * 初始化服务
     */
    fun initialize()

    /**
     * 关闭服务
     */
    fun shutdown()
}
