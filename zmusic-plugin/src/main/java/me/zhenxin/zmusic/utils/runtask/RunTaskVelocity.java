package me.zhenxin.zmusic.utils.runtask;

import me.zhenxin.zmusic.ZMusicVelocity;

public class RunTaskVelocity implements RunTask {

    @Override
    public void run(Runnable runnable) {

    }

    @Override
    public void runAsync(Runnable runnable) {
        ZMusicVelocity.plugin.getServer().getScheduler().buildTask(ZMusicVelocity.plugin, runnable).schedule();
    }
}
