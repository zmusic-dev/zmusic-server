package me.zhenxin.zmusic;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
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

        ZMusicRuntime.setup(dataDirectory.toFile().getAbsolutePath());
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) { ZMusicRuntime.initialize(dataDirectory.toFile().getAbsolutePath(), VelocityPluginBridge.class); VelocityPluginBridge.onEnable(server, dataDirectory, this, metricsFactory); }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) { VelocityPluginBridge.onDisable(); }
}
