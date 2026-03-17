package me.zhenxin.zmusic.platform

import me.zhenxin.zmusic.api.service.MessageService
import me.zhenxin.zmusic.api.service.PlatformService
import me.zhenxin.zmusic.api.service.PlayerService
import me.zhenxin.zmusic.api.service.PluginMessageService
import me.zhenxin.zmusic.api.service.SchedulerService
import me.zhenxin.zmusic.currentPlatform
import me.zhenxin.zmusic.enums.Platform
import me.zhenxin.zmusic.platform.service.BungeeMessageService
import me.zhenxin.zmusic.platform.service.BungeePlayerService
import me.zhenxin.zmusic.platform.service.BungeePluginMessageService
import me.zhenxin.zmusic.platform.service.BungeeSchedulerService
import net.md_5.bungee.api.plugin.Plugin

/**
 * BungeeCord 平台服务聚合实现
 *
 * @author 真心
 * @since 2024/12/9
 */
class BungeePlatformService(private val plugin: Plugin) : PlatformService {

    override val platform: Platform get() = currentPlatform

    override val messageService: MessageService by lazy { BungeeMessageService() }

    override val playerService: PlayerService by lazy { BungeePlayerService() }

    override val schedulerService: SchedulerService by lazy { BungeeSchedulerService(plugin) }

    override val pluginMessageService: PluginMessageService by lazy { BungeePluginMessageService(plugin) }

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
