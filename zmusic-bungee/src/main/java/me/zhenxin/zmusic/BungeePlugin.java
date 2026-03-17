package me.zhenxin.zmusic;

import me.zhenxin.zmusic.dependencies.RuntimeDependency;
import me.zhenxin.zmusic.enums.Platform;
import me.zhenxin.zmusic.platform.BungeeLoggerImpl;
import me.zhenxin.zmusic.platform.BungeePlatformService;
import me.zhenxin.zmusic.platform.command.BungeeCommandRegistrar;
import net.md_5.bungee.api.plugin.Plugin;
import org.bstats.bungeecord.Metrics;

/**
 * BungeeCord 入口类
 *
 * @author 真心
 * @since 2023/8/28 12:26
 */
@SuppressWarnings({"SpellCheckingInspection", "unused"})
@RuntimeDependency(
        value = "!org.bstats:bstats-bungeecord:" + ZMusicConstants.BSTATS_VERSION,
        test = "!me.zhenxin.zmusic.library.bstats.bungeecord.Metrics",
        relocate = {"!org.bstats.", "!me.zhenxin.zmusic.library.bstats."}
)
public class BungeePlugin extends Plugin {

    @Override
    public void onLoad() {
        ZMusicRuntime.setup(getDataFolder().getAbsolutePath(), BungeePlugin.class);
    }

    @Override
    public void onEnable() {
        ZMusicKt.setLogger(new BungeeLoggerImpl(getProxy().getConsole()));
        ZMusicKt.setDataFolder(getDataFolder());
        ZMusicKt.setCurrentPlatform(Platform.BUNGEE);
        ZMusicKt.setPlatformService(new BungeePlatformService(this));
        new Metrics(this, 8864);
        ZMusic.INSTANCE.onEnable();
        new BungeeCommandRegistrar(this).register();
    }

    @Override
    public void onDisable() {
        ZMusic.INSTANCE.onDisable();
    }
}
