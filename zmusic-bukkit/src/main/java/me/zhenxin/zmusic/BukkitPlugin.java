package me.zhenxin.zmusic;

import me.zhenxin.zmusic.dependencies.annotation.RuntimeDependency;
import me.zhenxin.zmusic.enums.Platform;
import me.zhenxin.zmusic.platform.BukkitLoggerImpl;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * ZMusic Bukkit 主入口
 *
 * @author 真心
 * @since 2022/7/14 11:35
 */
@SuppressWarnings({"SpellCheckingInspection", "unused"})
@RuntimeDependency(
        value = "!org.bstats:bstats-bukkit:" + ZMusicConstants.BSTATS_VERSION,
        test = "!me.zhenxin.zmusic.library.bstats.bukkit.Metrics",
        relocate = {"!org.bstats.", "!me.zhenxin.zmusic.library.bstats."}
)
public class BukkitPlugin extends JavaPlugin {

    @Override
    public void onLoad() {
        ZMusicRuntime.setup(getDataFolder().getAbsolutePath(), getLogger(), BukkitPlugin.class);
    }

    @Override
    public void onEnable() {
        ZMusicKt.setLogger(new BukkitLoggerImpl(getServer().getConsoleSender()));
        ZMusicKt.setDataFolder(getDataFolder());
        ZMusicKt.setPlatform(Platform.BUKKIT);
        new Metrics(this, 7291);
        ZMusic.INSTANCE.onEnable();
    }

    @Override
    public void onDisable() {
        ZMusic.INSTANCE.onDisable();
    }
}
