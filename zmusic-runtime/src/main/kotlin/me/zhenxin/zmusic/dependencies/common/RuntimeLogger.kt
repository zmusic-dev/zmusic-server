package me.zhenxin.zmusic.dependencies.common

object RuntimeLogger {
    @Volatile
    private var delegate: DependencyLogger = JdkDependencyLogger()

    @JvmStatic
    fun setLogger(logger: DependencyLogger) {
        delegate = logger
    }

    @JvmStatic
    fun getLogger(): DependencyLogger {
        return delegate
    }

    @JvmStatic
    fun info(message: String, vararg args: Any?) {
        delegate.info(message, *args)
    }

    @JvmStatic
    fun warning(message: String, vararg args: Any?) {
        delegate.warning(message, *args)
    }

    @JvmStatic
    fun error(message: String, vararg args: Any?) {
        delegate.error(message, *args)
    }

    @JvmStatic
    fun debug(message: String, vararg args: Any?) {
        delegate.debug(message, *args)
    }

    @JvmStatic
    fun logDependencyLoadStart(dependency: String): Long {
        return delegate.logDependencyLoadStart(dependency)
    }

    @JvmStatic
    fun logDependencyLoadSuccess(dependency: String, startTime: Long) {
        delegate.logDependencyLoadSuccess(dependency, startTime)
    }

    @JvmStatic
    fun logDependencyLoadFailure(dependency: String, startTime: Long, error: Throwable) {
        delegate.logDependencyLoadFailure(dependency, startTime, error)
    }

    @JvmStatic
    fun logDependencyStats() {
        delegate.logDependencyStats()
    }

    @JvmStatic
    fun resetStats() {
        delegate.resetStats()
    }

    @JvmStatic
    fun getLoadedCount(): Int {
        return delegate.getLoadedCount()
    }

    @JvmStatic
    fun getFailedCount(): Int {
        return delegate.getFailedCount()
    }

    @JvmStatic
    fun getTotalLoadTime(): Long {
        return delegate.getTotalLoadTime()
    }
}
