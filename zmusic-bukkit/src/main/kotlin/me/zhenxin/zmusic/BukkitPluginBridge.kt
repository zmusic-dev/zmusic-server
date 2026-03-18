package me.zhenxin.zmusic

import me.zhenxin.zmusic.enums.Platform
import me.zhenxin.zmusic.platform.BukkitLoggerImpl
import me.zhenxin.zmusic.platform.BukkitPlatformService
import me.zhenxin.zmusic.platform.command.BukkitCommandRegistrar
import org.bstats.bukkit.Metrics
import org.bukkit.plugin.java.JavaPlugin

object BukkitPluginBridge {

    @JvmStatic
    fun onEnable(plugin: JavaPlugin) {
        logger = BukkitLoggerImpl(plugin.server.consoleSender)
        dataFolder = plugin.dataFolder
        currentPlatform = detectPlatform()
        platformService = BukkitPlatformService(plugin)
        Metrics(plugin, 7291)
        ZMusic.onEnable()
        BukkitCommandRegistrar(plugin).register()
    }

    @JvmStatic
    fun onDisable() {
        ZMusic.onDisable()
    }

    private fun detectPlatform(): Platform {
        return if (runCatching { Class.forName("io.papermc.paper.threadedregions.RegionizedServer") }.isSuccess) {
            Platform.FOLIA
        } else {
            Platform.BUKKIT
        }
    }
}
