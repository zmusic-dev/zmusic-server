package me.zhenxin.zmusic.platform.service

import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.scheduler.ScheduledTask
import me.zhenxin.zmusic.api.entity.ZPlayer
import me.zhenxin.zmusic.api.service.SchedulerService
import me.zhenxin.zmusic.api.service.TaskHandle
import java.time.Duration

/**
 * Velocity 任务句柄
 */
class VelocityTaskHandle(private val task: ScheduledTask) : TaskHandle {
    private var cancelled = false

    override fun cancel() {
        task.cancel()
        cancelled = true
    }

    override fun isCancelled(): Boolean = cancelled
}

/**
 * Velocity 调度服务实现
 *
 * @author 真心
 * @since 2024/12/9
 */
class VelocitySchedulerService(
    private val server: ProxyServer,
    private val plugin: Any
) : SchedulerService {

    private val scheduler get() = server.scheduler

    override fun runTask(task: Runnable) {
        scheduler.buildTask(plugin, task).schedule()
    }

    override fun runTaskAsync(task: Runnable) {
        // Velocity 所有任务默认都是异步的
        scheduler.buildTask(plugin, task).schedule()
    }

    override fun runTaskLater(delay: Long, task: Runnable): TaskHandle {
        // 转换 tick 为毫秒（20 tick = 1秒）
        val delayMs = delay * 50
        return VelocityTaskHandle(
            scheduler.buildTask(plugin, task)
                .delay(Duration.ofMillis(delayMs))
                .schedule()
        )
    }

    override fun runTaskLaterAsync(delay: Long, task: Runnable): TaskHandle {
        val delayMs = delay * 50
        return VelocityTaskHandle(
            scheduler.buildTask(plugin, task)
                .delay(Duration.ofMillis(delayMs))
                .schedule()
        )
    }

    override fun runTaskTimer(delay: Long, period: Long, task: Runnable): TaskHandle {
        val delayMs = delay * 50
        val periodMs = period * 50
        return VelocityTaskHandle(
            scheduler.buildTask(plugin, task)
                .delay(Duration.ofMillis(delayMs))
                .repeat(Duration.ofMillis(periodMs))
                .schedule()
        )
    }

    override fun runTaskTimerAsync(delay: Long, period: Long, task: Runnable): TaskHandle {
        val delayMs = delay * 50
        val periodMs = period * 50
        return VelocityTaskHandle(
            scheduler.buildTask(plugin, task)
                .delay(Duration.ofMillis(delayMs))
                .repeat(Duration.ofMillis(periodMs))
                .schedule()
        )
    }

    override fun runAtEntity(player: ZPlayer, task: Runnable) {
        // Velocity 没有实体概念，直接执行
        runTask(task)
    }
}
