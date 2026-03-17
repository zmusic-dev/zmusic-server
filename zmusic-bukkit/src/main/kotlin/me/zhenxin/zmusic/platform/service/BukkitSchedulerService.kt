package me.zhenxin.zmusic.platform.service

import me.zhenxin.zmusic.api.entity.ZPlayer
import me.zhenxin.zmusic.api.service.SchedulerService
import me.zhenxin.zmusic.api.service.TaskHandle
import me.zhenxin.zmusic.currentPlatform
import me.zhenxin.zmusic.enums.Platform
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import java.util.function.Consumer

/**
 * Bukkit 任务句柄
 */
class BukkitTaskHandle(private val task: BukkitTask) : TaskHandle {
    override fun cancel() = task.cancel()
    override fun isCancelled(): Boolean = task.isCancelled
}

/**
 * Bukkit 调度服务实现
 *
 * @author 真心
 * @since 2024/12/9
 */
class BukkitSchedulerService(private val plugin: Plugin) : SchedulerService {

    private val scheduler get() = Bukkit.getScheduler()

    override fun runTask(task: Runnable) {
        scheduler.runTask(plugin, task)
    }

    override fun runTaskAsync(task: Runnable) {
        scheduler.runTaskAsynchronously(plugin, task)
    }

    override fun runTaskLater(delay: Long, task: Runnable): TaskHandle {
        return BukkitTaskHandle(scheduler.runTaskLater(plugin, task, delay))
    }

    override fun runTaskLaterAsync(delay: Long, task: Runnable): TaskHandle {
        return BukkitTaskHandle(scheduler.runTaskLaterAsynchronously(plugin, task, delay))
    }

    override fun runTaskTimer(delay: Long, period: Long, task: Runnable): TaskHandle {
        return BukkitTaskHandle(scheduler.runTaskTimer(plugin, task, delay, period))
    }

    override fun runTaskTimerAsync(delay: Long, period: Long, task: Runnable): TaskHandle {
        return BukkitTaskHandle(scheduler.runTaskTimerAsynchronously(plugin, task, delay, period))
    }

    override fun runAtEntity(player: ZPlayer, task: Runnable) {
        if (currentPlatform == Platform.FOLIA) {
            runAtEntityFolia(player, task)
        } else {
            runTask(task)
        }
    }

    private fun runAtEntityFolia(player: ZPlayer, task: Runnable) {
        try {
            val bukkitPlayer: Player = player.getPlatformPlayer()
            // 使用反射调用 Folia API，避免编译时依赖
            // API: EntityScheduler.run(Plugin plugin, Consumer<ScheduledTask> task, Runnable retired)
            val entityScheduler = bukkitPlayer.javaClass.getMethod("getScheduler").invoke(bukkitPlayer)
            val runMethod = entityScheduler.javaClass.getMethod(
                "run",
                Plugin::class.java,
                Consumer::class.java,
                Runnable::class.java
            )
            // Consumer<ScheduledTask> 的泛型在运行时被擦除，所以这里用 Consumer<Any?> 即可
            val consumer = Consumer<Any?> { _ -> task.run() }
            runMethod.invoke(entityScheduler, plugin, consumer, null)
        } catch (e: Exception) {
            // Fallback to sync task if Folia API is not available
            runTask(task)
        }
    }
}
