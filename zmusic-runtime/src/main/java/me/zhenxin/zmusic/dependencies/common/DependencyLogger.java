package me.zhenxin.zmusic.dependencies.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 依赖加载日志接口
 * 允许自定义日志实现，可以集成到不同的日志框架
 *
 * @author ZMusic Team
 * @since 4.0.0
 */
public interface DependencyLogger {

    /**
     * 输出 Info 级别日志
     *
     * @param message 日志消息
     * @param args    消息参数（可选）
     */
    void info(@NotNull String message, @Nullable Object... args);

    /**
     * 输出 Warning 级别日志
     *
     * @param message 日志消息
     * @param args    消息参数（可选）
     */
    void warning(@NotNull String message, @Nullable Object... args);

    /**
     * 输出 Error 级别日志
     *
     * @param message 日志消息
     * @param args    消息参数（可选）
     */
    void error(@NotNull String message, @Nullable Object... args);

    /**
     * 输出 Debug 级别日志
     * 仅在详细模式 (verbose) 下输出
     *
     * @param message 日志消息
     * @param args    消息参数（可选）
     */
    void debug(@NotNull String message, @Nullable Object... args);

    /**
     * 记录依赖加载开始
     *
     * @param dependency Maven 坐标 (groupId:artifactId:version)
     * @return 开始时间戳（毫秒）
     */
    long logDependencyLoadStart(@NotNull String dependency);

    /**
     * 记录依赖加载成功
     *
     * @param dependency Maven 坐标
     * @param startTime  开始时间戳
     */
    void logDependencyLoadSuccess(@NotNull String dependency, long startTime);

    /**
     * 记录依赖加载失败
     *
     * @param dependency Maven 坐标
     * @param startTime  开始时间戳
     * @param error      失败异常
     */
    void logDependencyLoadFailure(@NotNull String dependency, long startTime, @NotNull Throwable error);

    /**
     * 输出依赖加载统计信息
     */
    void logDependencyStats();

    /**
     * 重置统计信息
     */
    void resetStats();

    /**
     * 获取成功加载的依赖数量
     */
    int getLoadedCount();

    /**
     * 获取加载失败的依赖数量
     */
    int getFailedCount();

    /**
     * 获取总加载时间（毫秒）
     */
    long getTotalLoadTime();
}
