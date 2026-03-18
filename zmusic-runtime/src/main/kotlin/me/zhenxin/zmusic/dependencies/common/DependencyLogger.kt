package me.zhenxin.zmusic.dependencies.common

interface DependencyLogger {
    fun info(message: String, vararg args: Any?)

    fun warning(message: String, vararg args: Any?)

    fun error(message: String, vararg args: Any?)

    fun debug(message: String, vararg args: Any?)

    fun logDependencyLoadStart(dependency: String): Long

    fun logDependencyLoadSuccess(dependency: String, startTime: Long)

    fun logDependencyLoadFailure(dependency: String, startTime: Long, error: Throwable)

    fun logDependencyStats()

    fun resetStats()

    fun getLoadedCount(): Int

    fun getFailedCount(): Int

    fun getTotalLoadTime(): Long
}
