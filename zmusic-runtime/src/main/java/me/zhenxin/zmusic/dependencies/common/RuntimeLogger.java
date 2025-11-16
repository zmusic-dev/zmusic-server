package me.zhenxin.zmusic.dependencies.common;

import org.jetbrains.annotations.NotNull;

/**
 * 运行时日志工具类（委托模式）
 * 所有静态方法委托给底层的 {@link DependencyLogger} 实例
 *
 * <p>向后兼容性：保留所有静态方法，现有代码无需修改
 * <p>新代码推荐直接使用 {@link DependencyLogger} 接口
 *
 * @author zhenxin
 * @since 2024
 */
public class RuntimeLogger {

    /**
     * 默认日志实例
     */
    private static DependencyLogger delegate = new JdkDependencyLogger();

    /**
     * 设置自定义日志实例
     * 允许替换默认的 JDK 日志实现
     *
     * @param logger 自定义日志实例
     */
    public static void setLogger(@NotNull DependencyLogger logger) {
        delegate = logger;
    }

    /**
     * 获取当前日志实例
     */
    @NotNull
    public static DependencyLogger getLogger() {
        return delegate;
    }

    // ==================== 委托方法（向后兼容） ====================

    /**
     * 输出 Info 日志
     *
     * @param message 日志内容
     */
    public static void info(String message, Object... args) {
        delegate.info(message, args);
    }

    /**
     * 输出 Warning 日志
     *
     * @param message 日志内容
     */
    public static void warning(String message, Object... args) {
        delegate.warning(message, args);
    }

    /**
     * 输出 Error 日志
     *
     * @param message 日志内容
     */
    public static void error(String message, Object... args) {
        delegate.error(message, args);
    }

    /**
     * 输出调试日志（仅在详细模式下）
     *
     * @param message 日志内容
     */
    public static void debug(String message, Object... args) {
        delegate.debug(message, args);
    }

    /**
     * 记录依赖加载开始
     */
    public static long logDependencyLoadStart(String dependency) {
        return delegate.logDependencyLoadStart(dependency);
    }

    /**
     * 记录依赖加载成功
     */
    public static void logDependencyLoadSuccess(String dependency, long startTime) {
        delegate.logDependencyLoadSuccess(dependency, startTime);
    }

    /**
     * 记录依赖加载失败
     */
    public static void logDependencyLoadFailure(String dependency, long startTime, Throwable error) {
        delegate.logDependencyLoadFailure(dependency, startTime, error);
    }

    /**
     * 输出依赖加载统计信息
     */
    public static void logDependencyStats() {
        delegate.logDependencyStats();
    }

    /**
     * 重置统计信息
     */
    public static void resetStats() {
        delegate.resetStats();
    }

    /**
     * 获取成功加载的依赖数量
     */
    public static int getLoadedCount() {
        return delegate.getLoadedCount();
    }

    /**
     * 获取加载失败的依赖数量
     */
    public static int getFailedCount() {
        return delegate.getFailedCount();
    }

    /**
     * 获取总加载时间（毫秒）
     */
    public static long getTotalLoadTime() {
        return delegate.getTotalLoadTime();
    }
}
