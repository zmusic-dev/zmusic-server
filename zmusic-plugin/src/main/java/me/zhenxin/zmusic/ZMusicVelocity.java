package me.zhenxin.zmusic;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import me.zhenxin.zmusic.command.CmdVelocity;
import me.zhenxin.zmusic.config.Config;
import me.zhenxin.zmusic.event.EventVelocity;
import me.zhenxin.zmusic.utils.CookieUtils;
import me.zhenxin.zmusic.utils.log.LogVelocity;
import me.zhenxin.zmusic.utils.message.MessageVelocity;
import me.zhenxin.zmusic.utils.mod.SendVelocity;
import me.zhenxin.zmusic.utils.music.Music;
import me.zhenxin.zmusic.utils.player.PlayerVelocity;
import me.zhenxin.zmusic.utils.runtask.RunTaskVelocity;
import org.bstats.velocity.Metrics;

import java.nio.file.Path;
import java.util.logging.Logger;

@Plugin(id = "zmusic", name = "ZMusic", version = "2.10.4", description = "跨服音乐播放插件", authors = {"真心"})
public class ZMusicVelocity {

    public static ZMusicVelocity plugin;
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private final Metrics.Factory metricsFactory;

    @Inject
    public ZMusicVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory, Metrics.Factory metricsFactory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.metricsFactory = metricsFactory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        ZMusic.log = new LogVelocity(server.getConsoleCommandSource());
        plugin = this;
        ZMusic.isBC = true; // Velocity acts like BungeeCord for our purposes
        ZMusic.runTask = new RunTaskVelocity();
        ZMusic.message = new MessageVelocity();
        ZMusic.music = new Music();
        ZMusic.send = new SendVelocity();
        ZMusic.player = new PlayerVelocity();
        ZMusic.dataFolder = dataDirectory.toFile();
        if (!ZMusic.dataFolder.exists()) {
            ZMusic.dataFolder.mkdir();
        }
        Config.debug = true;
        ZMusic.thisVer = "2.10.4";
        ZMusic.log.sendNormalMessage("正在加载中....");
        CookieUtils.initCookieManager();
        
        // Initialize bStats metrics (service ID for ZMusic Velocity)
        metricsFactory.make(this, 19999); // TODO: Get actual service ID from bStats
        
        // Register plugin channels
        server.getChannelRegistrar().register("zmusic:channel");
        server.getChannelRegistrar().register("allmusic:channel");
        server.getChannelRegistrar().register("AudioBuffer");
        
        // Register command and events
        server.getCommandManager().register("zm", new CmdVelocity(), "zmusic", "music");
        server.getEventManager().register(this, new EventVelocity());
        
        ZMusic.loadEnd(server.getConsoleCommandSource());
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        ZMusic.disable();
    }

    public ProxyServer getServer() {
        return server;
    }
}
