package me.zhenxin.zmusic.dependencies.common

import me.zhenxin.zmusic.dependencies.DependencyConfiguration
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.logging.Level
import java.util.logging.Logger

class JdkDependencyLogger(
    loggerName: String = "ZMusic",
    private val verboseLogging: Boolean = DependencyConfiguration.getGlobal().verboseLogging,
) : DependencyLogger {
    private val logger = Logger.getLogger(loggerName)
    private val loadedDependencies = AtomicInteger(0)
    private val failedDependencies = AtomicInteger(0)
    private val totalLoadTime = AtomicLong(0)

    override fun info(message: String, vararg args: Any?) {
        logger.log(Level.INFO, message, args)
    }

    override fun warning(message: String, vararg args: Any?) {
        logger.log(Level.WARNING, message, args)
    }

    override fun error(message: String, vararg args: Any?) {
        logger.log(Level.SEVERE, message, args)
    }

    override fun debug(message: String, vararg args: Any?) {
        if (verboseLogging) {
            logger.log(Level.INFO, "[DEBUG] $message", args)
        }
    }

    override fun logDependencyLoadStart(dependency: String): Long {
        val parts = parseDependency(dependency)
        debug(
            PrimitiveIO.t("开始加载依赖: {0}:{1}:{2}", "Loading dependency: {0}:{1}:{2}"),
            parts[0],
            parts[1],
            parts[2],
        )
        return System.currentTimeMillis()
    }

    override fun logDependencyLoadSuccess(dependency: String, startTime: Long) {
        val duration = System.currentTimeMillis() - startTime
        loadedDependencies.incrementAndGet()
        totalLoadTime.addAndGet(duration)
        val parts = parseDependency(dependency)
        info(
            PrimitiveIO.t(
                "✓ 依赖加载成功: {0}:{1}:{2} (耗时: {3}ms)",
                "✓ Dependency loaded: {0}:{1}:{2} ({3}ms)",
            ),
            parts[0],
            parts[1],
            parts[2],
            duration,
        )
    }

    override fun logDependencyLoadFailure(dependency: String, startTime: Long, error: Throwable) {
        val duration = System.currentTimeMillis() - startTime
        failedDependencies.incrementAndGet()
        totalLoadTime.addAndGet(duration)
        val parts = parseDependency(dependency)
        warning(
            PrimitiveIO.t(
                "✗ 依赖加载失败: {0}:{1}:{2} (耗时: {3}ms) - {4}",
                "✗ Failed to load dependency: {0}:{1}:{2} ({3}ms) - {4}",
            ),
            parts[0],
            parts[1],
            parts[2],
            duration,
            error.message,
        )
    }

    override fun logDependencyStats() {
        val loaded = loadedDependencies.get()
        val failed = failedDependencies.get()
        val totalTime = totalLoadTime.get()
        if (loaded > 0 || failed > 0) {
            info(
                PrimitiveIO.t(
                    "依赖加载统计: 成功 {0}, 失败 {1}, 总耗时 {2}ms",
                    "Dependency loading summary: {0} loaded, {1} failed, total time {2}ms",
                ),
                loaded,
                failed,
                totalTime,
            )
        }
    }

    override fun resetStats() {
        loadedDependencies.set(0)
        failedDependencies.set(0)
        totalLoadTime.set(0)
    }

    override fun getLoadedCount(): Int {
        return loadedDependencies.get()
    }

    override fun getFailedCount(): Int {
        return failedDependencies.get()
    }

    override fun getTotalLoadTime(): Long {
        return totalLoadTime.get()
    }

    private fun parseDependency(dependency: String): Array<String> {
        return try {
            dependency.split(':').let { parts ->
                if (parts.size >= 3) {
                    arrayOf(parts[0], parts[1], parts[2])
                } else {
                    arrayOf(dependency, "", "")
                }
            }
        } catch (_: Exception) {
            arrayOf(dependency, "", "")
        }
    }
}
