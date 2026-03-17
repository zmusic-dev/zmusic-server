package me.zhenxin.zmusic.platform

import com.velocitypowered.api.proxy.ProxyServer
import me.zhenxin.zmusic.api.service.MessageService
import me.zhenxin.zmusic.api.service.PlatformService
import me.zhenxin.zmusic.api.service.PlayerService
import me.zhenxin.zmusic.api.service.PluginMessageService
import me.zhenxin.zmusic.api.service.SchedulerService
import me.zhenxin.zmusic.currentPlatform
import me.zhenxin.zmusic.enums.Platform
import me.zhenxin.zmusic.platform.service.VelocityMessageService
import me.zhenxin.zmusic.platform.service.VelocityPlayerService
import me.zhenxin.zmusic.platform.service.VelocityPluginMessageService
import me.zhenxin.zmusic.platform.service.VelocitySchedulerService

/**
 * Velocity 平台服务聚合实现
 *
 * @author 真心
 * @since 2024/12/9
 */
class VelocityPlatformService(
    private val server: ProxyServer,
    private val plugin: Any
) : PlatformService {

    override val platform: Platform get() = currentPlatform

    override val messageService: MessageService by lazy { VelocityMessageService(server) }

    override val playerService: PlayerService by lazy { VelocityPlayerService(server) }

    override val schedulerService: SchedulerService by lazy { VelocitySchedulerService(server, plugin) }

    override val pluginMessageService: PluginMessageService by lazy { VelocityPluginMessageService(server, plugin) }

    override fun initialize() {
        // 注册 ZMusic 插件通道
        pluginMessageService.registerChannel("zmusic:play")
        pluginMessageService.registerChannel("zmusic:stop")
        pluginMessageService.registerChannel("zmusic:info")
    }

    override fun shutdown() {
        // 注销插件通道
        pluginMessageService.unregisterChannel("zmusic:play")
        pluginMessageService.unregisterChannel("zmusic:stop")
        pluginMessageService.unregisterChannel("zmusic:info")
    }
}
