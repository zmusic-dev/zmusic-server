package me.zhenxin.zmusic.utils.runtask;

import me.zhenxin.zmusic.ZMusicVelocity;

public class RunTaskVelocity implements RunTask {

    @Override
    public void run(Runnable runnable) {
        throw new UnsupportedOperationException("Velocity 不支持同步任务，请使用 runAsync");
    }

    @Override
    public void runAsync(Runnable runnable) {
        ZMusicVelocity.plugin.getServer().getScheduler().buildTask(ZMusicVelocity.plugin, runnable).schedule();
    }
}
