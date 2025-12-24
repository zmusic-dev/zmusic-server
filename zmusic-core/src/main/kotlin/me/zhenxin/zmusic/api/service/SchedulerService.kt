package me.zhenxin.zmusic.api.service

import me.zhenxin.zmusic.api.entity.ZPlayer

/**
 * 任务句柄
 */
interface TaskHandle {
    /**
     * 取消任务
     */
    fun cancel()

    /**
     * 任务是否已取消
     */
    fun isCancelled(): Boolean
}

/**
 * 任务调度服务接口
 *
 * @author 真心
 * @since 2024/12/9
 */
interface SchedulerService {
    /**
     * 在主线程执行任务
     */
    fun runTask(task: Runnable)

    /**
     * 在异步线程执行任务
     */
    fun runTaskAsync(task: Runnable)

    /**
     * 延迟执行任务（主线程）
     *
     * @param delay 延迟时间（tick，20tick = 1秒）
     */
    fun runTaskLater(delay: Long, task: Runnable): TaskHandle

    /**
     * 延迟执行任务（异步）
     *
     * @param delay 延迟时间（tick）
     */
    fun runTaskLaterAsync(delay: Long, task: Runnable): TaskHandle

    /**
     * 定时执行任务（主线程）
     *
     * @param delay 首次延迟时间（tick）
     * @param period 周期时间（tick）
     */
    fun runTaskTimer(delay: Long, period: Long, task: Runnable): TaskHandle

    /**
     * 定时执行任务（异步）
     *
     * @param delay 首次延迟时间（tick）
     * @param period 周期时间（tick）
     */
    fun runTaskTimerAsync(delay: Long, period: Long, task: Runnable): TaskHandle

    /**
     * 在玩家所在区域执行任务（Folia 兼容）
     * 对于非 Folia 服务器，等同于 runTask
     */
    fun runAtEntity(player: ZPlayer, task: Runnable)
}
