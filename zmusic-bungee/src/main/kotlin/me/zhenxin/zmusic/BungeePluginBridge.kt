package me.zhenxin.zmusic

import me.zhenxin.zmusic.enums.Platform
import me.zhenxin.zmusic.platform.BungeeLoggerImpl
import me.zhenxin.zmusic.platform.BungeePlatformService
import me.zhenxin.zmusic.platform.command.BungeeCommandRegistrar
import net.md_5.bungee.api.plugin.Plugin
import org.bstats.bungeecord.Metrics

object BungeePluginBridge {

    @JvmStatic
    fun onEnable(plugin: Plugin) {
        logger = BungeeLoggerImpl(plugin.proxy.console)
        dataFolder = plugin.dataFolder
        currentPlatform = Platform.BUNGEE
        platformService = BungeePlatformService(plugin)
        Metrics(plugin, 8864)
        ZMusic.onEnable()
        BungeeCommandRegistrar(plugin).register()
    }

    @JvmStatic
    fun onDisable() {
        ZMusic.onDisable()
    }
}
