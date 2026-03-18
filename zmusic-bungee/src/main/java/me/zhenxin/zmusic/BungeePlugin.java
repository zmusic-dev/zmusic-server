package me.zhenxin.zmusic;

import net.md_5.bungee.api.plugin.Plugin;

/**
 * BungeeCord 入口类
 *
 * @author 真心
 * @since 2023/8/28 12:26
 */
@SuppressWarnings({"SpellCheckingInspection", "unused"})
public class BungeePlugin extends Plugin {

    @Override
    public void onLoad() { ZMusicRuntime.setup(getDataFolder().getAbsolutePath()); }

    @Override
    public void onEnable() { ZMusicRuntime.initialize(getDataFolder().getAbsolutePath(), BungeePluginBridge.class); BungeePluginBridge.onEnable(this); }

    @Override
    public void onDisable() { BungeePluginBridge.onDisable(); }
}
