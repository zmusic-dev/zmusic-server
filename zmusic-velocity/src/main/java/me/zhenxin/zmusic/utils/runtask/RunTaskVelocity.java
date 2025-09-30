package me.zhenxin.zmusic.utils.runtask;

import me.zhenxin.zmusic.ZMusicVelocity;

public class RunTaskVelocity implements RunTask {

    @Override
    public void run(Runnable runnable) {
        // For Velocity, we run tasks synchronously on the main thread
        runnable.run();
    }

    @Override
    public void runAsync(Runnable runnable) {
        ZMusicVelocity.plugin.getServer().getScheduler().buildTask(ZMusicVelocity.plugin, runnable).schedule();
    }
}