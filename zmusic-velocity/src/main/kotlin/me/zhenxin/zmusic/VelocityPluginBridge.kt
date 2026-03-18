package me.zhenxin.zmusic

import com.velocitypowered.api.proxy.ProxyServer
import me.zhenxin.zmusic.enums.Platform
import me.zhenxin.zmusic.platform.VelocityLoggerImpl
import me.zhenxin.zmusic.platform.VelocityPlatformService
import me.zhenxin.zmusic.platform.command.VelocityCommandRegistrar
import org.bstats.velocity.Metrics
import java.nio.file.Path

object VelocityPluginBridge {

    @JvmStatic
    fun onEnable(server: ProxyServer, dataDirectory: Path, plugin: Any, metricsFactory: Metrics.Factory) {
        logger = VelocityLoggerImpl(server.consoleCommandSource)
        dataFolder = dataDirectory.toFile()
        currentPlatform = Platform.VELOCITY
        platformService = VelocityPlatformService(server, plugin)
        metricsFactory.make(plugin, 12426)
        ZMusic.onEnable()
        VelocityCommandRegistrar(server).register()
    }

    @JvmStatic
    fun onDisable() {
        ZMusic.onDisable()
    }
}
