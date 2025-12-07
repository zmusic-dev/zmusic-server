package me.zhenxin.zmusic.utils.runtask;

import me.zhenxin.zmusic.ZMusicVelocity;

public class RunTaskVelocity implements RunTask {

    @Override
    public void run(Runnable runnable) {
        // Velocity 没有主线程概念，直接执行
        runnable.run();
    }

    @Override
    public void runAsync(Runnable runnable) {
        ZMusicVelocity.server.getScheduler()
                .buildTask(ZMusicVelocity.plugin, runnable)
                .schedule();
    }
}
