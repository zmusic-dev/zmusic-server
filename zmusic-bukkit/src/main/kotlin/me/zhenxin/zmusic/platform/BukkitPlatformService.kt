package me.zhenxin.zmusic.platform

import me.zhenxin.zmusic.api.service.MessageService
import me.zhenxin.zmusic.api.service.PlatformService
import me.zhenxin.zmusic.api.service.PlayerService
import me.zhenxin.zmusic.api.service.PluginMessageService
import me.zhenxin.zmusic.api.service.SchedulerService
import me.zhenxin.zmusic.currentPlatform
import me.zhenxin.zmusic.enums.Platform
import me.zhenxin.zmusic.platform.service.BukkitMessageService
import me.zhenxin.zmusic.platform.service.BukkitPlayerService
import me.zhenxin.zmusic.platform.service.BukkitPluginMessageService
import me.zhenxin.zmusic.platform.service.BukkitSchedulerService
import org.bukkit.plugin.Plugin

/**
 * Bukkit 平台服务聚合实现
 *
 * @author 真心
 * @since 2024/12/9
 */
class BukkitPlatformService(private val plugin: Plugin) : PlatformService {

    override val platform: Platform get() = currentPlatform

    override val messageService: MessageService by lazy { BukkitMessageService() }

    override val playerService: PlayerService by lazy { BukkitPlayerService() }

    override val schedulerService: SchedulerService by lazy { BukkitSchedulerService(plugin) }

    override val pluginMessageService: PluginMessageService by lazy { BukkitPluginMessageService(plugin) }

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
