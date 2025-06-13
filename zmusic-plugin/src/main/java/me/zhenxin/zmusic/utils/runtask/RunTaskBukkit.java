package me.zhenxin.zmusic.utils.runtask;

import me.zhenxin.zmusic.ZMusic;
import me.zhenxin.zmusic.ZMusicBukkit;
import org.bukkit.Bukkit;

public class RunTaskBukkit implements RunTask {

    @Override
    public void run(Runnable runnable) {
        if (ZMusic.isFolia) {
            new Thread(runnable).start();
        } else {
            Bukkit.getServer().getScheduler().runTask(ZMusicBukkit.plugin, runnable);
        }
    }

    @Override
    public void runAsync(Runnable runnable) {
        if (ZMusic.isFolia) {
            new Thread(runnable).start();
        } else {
            Bukkit.getServer().getScheduler().runTaskAsynchronously(ZMusicBukkit.plugin, runnable);
        }
    }
}
