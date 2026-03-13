package me.zhenxin.zmusic;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import me.zhenxin.zmusic.dependencies.RuntimeDependency;
import me.zhenxin.zmusic.enums.Platform;
import me.zhenxin.zmusic.platform.VelocityLoggerImpl;
import me.zhenxin.zmusic.platform.VelocityPlatformService;
import me.zhenxin.zmusic.platform.command.VelocityCommandRegistrar;
import org.bstats.velocity.Metrics;

import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * Velocity 入口类
 *
 * @author 真心
 * @since 2023/8/28 12:22
 */
@SuppressWarnings({"SpellCheckingInspection", "unused"})
@RuntimeDependency(
        value = "!org.bstats:bstats-velocity:" + ZMusicConstants.BSTATS_VERSION,
        test = "!me.zhenxin.zmusic.library.bstats.velocity.Metrics",
        relocate = {"!org.bstats.", "!me.zhenxin.zmusic.library.bstats."}
)
@Plugin(id = "zmusic")
public class VelocityPlugin {
    private final ProxyServer server;
    private final Path dataDirectory;
    private final Metrics.Factory metricsFactory;

    @Inject
    public VelocityPlugin(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory, Metrics.Factory metricsFactory) {
        this.server = server;
        this.dataDirectory = dataDirectory;
        this.metricsFactory = metricsFactory;

        ZMusicRuntime.setup(dataDirectory.toFile().getAbsolutePath(), VelocityPlugin.class);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        ZMusicKt.setLogger(new VelocityLoggerImpl(server.getConsoleCommandSource()));
        ZMusicKt.setDataFolder(dataDirectory.toFile());
        ZMusicKt.setCurrentPlatform(Platform.VELOCITY);
        ZMusicKt.setPlatformService(new VelocityPlatformService(server, this));
        metricsFactory.make(this, 12426);
        ZMusic.INSTANCE.onEnable();
        new VelocityCommandRegistrar(server).register();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        ZMusic.INSTANCE.onDisable();
    }
}
