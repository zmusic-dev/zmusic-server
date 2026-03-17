package me.zhenxin.zmusic.platform.service

import me.zhenxin.zmusic.api.entity.ZPlayer
import me.zhenxin.zmusic.api.service.SchedulerService
import me.zhenxin.zmusic.api.service.TaskHandle
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.api.scheduler.ScheduledTask
import java.util.concurrent.TimeUnit

/**
 * BungeeCord 任务句柄
 */
class BungeeTaskHandle(private val task: ScheduledTask) : TaskHandle {
    private var cancelled = false

    override fun cancel() {
        task.cancel()
        cancelled = true
    }

    override fun isCancelled(): Boolean = cancelled
}

/**
 * BungeeCord 调度服务实现
 * 注意：BungeeCord 所有任务都是异步的
 *
 * @author 真心
 * @since 2024/12/9
 */
class BungeeSchedulerService(private val plugin: Plugin) : SchedulerService {

    private val scheduler get() = ProxyServer.getInstance().scheduler

    override fun runTask(task: Runnable) {
        // BungeeCord 没有主线程概念，使用异步执行
        scheduler.runAsync(plugin, task)
    }

    override fun runTaskAsync(task: Runnable) {
        scheduler.runAsync(plugin, task)
    }

    override fun runTaskLater(delay: Long, task: Runnable): TaskHandle {
        // 转换 tick 为毫秒（20 tick = 1秒）
        val delayMs = delay * 50
        return BungeeTaskHandle(scheduler.schedule(plugin, task, delayMs, TimeUnit.MILLISECONDS))
    }

    override fun runTaskLaterAsync(delay: Long, task: Runnable): TaskHandle {
        val delayMs = delay * 50
        return BungeeTaskHandle(scheduler.schedule(plugin, task, delayMs, TimeUnit.MILLISECONDS))
    }

    override fun runTaskTimer(delay: Long, period: Long, task: Runnable): TaskHandle {
        val delayMs = delay * 50
        val periodMs = period * 50
        return BungeeTaskHandle(scheduler.schedule(plugin, task, delayMs, periodMs, TimeUnit.MILLISECONDS))
    }

    override fun runTaskTimerAsync(delay: Long, period: Long, task: Runnable): TaskHandle {
        val delayMs = delay * 50
        val periodMs = period * 50
        return BungeeTaskHandle(scheduler.schedule(plugin, task, delayMs, periodMs, TimeUnit.MILLISECONDS))
    }

    override fun runAtEntity(player: ZPlayer, task: Runnable) {
        // BungeeCord 没有实体概念，直接异步执行
        runTaskAsync(task)
    }
}
