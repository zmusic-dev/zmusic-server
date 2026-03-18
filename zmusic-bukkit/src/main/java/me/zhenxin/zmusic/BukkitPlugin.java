package me.zhenxin.zmusic;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * ZMusic Bukkit 主入口
 *
 * @author 真心
 * @since 2022/7/14 11:35
 */
@SuppressWarnings({"SpellCheckingInspection", "unused"})
public class BukkitPlugin extends JavaPlugin {

    @Override
    public void onLoad() { ZMusicRuntime.setup(getDataFolder().getAbsolutePath()); }

    @Override
    public void onDisable() { BukkitPluginBridge.onDisable(); }

    @Override
    public void onEnable() { ZMusicRuntime.initialize(getDataFolder().getAbsolutePath(), BukkitPluginBridge.class); BukkitPluginBridge.onEnable(this); }
}
