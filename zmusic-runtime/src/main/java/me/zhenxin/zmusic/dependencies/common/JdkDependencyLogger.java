package me.zhenxin.zmusic.dependencies.common;

import me.zhenxin.zmusic.dependencies.DependencyConfig;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import static me.zhenxin.zmusic.dependencies.common.PrimitiveIO.t;

/**
 * 基于 JDK java.util.logging 的日志实现
 * 这是默认的日志实现，适用于大多数 Minecraft 服务端环境
 *
 * @author ZMusic Team
 * @since 4.0.0
 */
public class JdkDependencyLogger implements DependencyLogger {

    private final Logger logger;
    private final boolean verboseLogging;

    // 统计信息（线程安全）
    private final AtomicInteger loadedDependencies = new AtomicInteger(0);
    private final AtomicInteger failedDependencies = new AtomicInteger(0);
    private final AtomicLong totalLoadTime = new AtomicLong(0);

    /**
     * 创建 JDK 日志实例
     *
     * @param loggerName     日志名称
     * @param verboseLogging 是否启用详细日志
     */
    public JdkDependencyLogger(@NotNull String loggerName, boolean verboseLogging) {
        this.logger = Logger.getLogger(loggerName);
        this.verboseLogging = verboseLogging;
    }

    /**
     * 创建默认的 JDK 日志实例
     * 使用 "ZMusic" 作为日志名称，从系统属性读取 verbose 设置
     */
    public JdkDependencyLogger() {
        this("ZMusic", DependencyConfig.VERBOSE_LOGGING);
    }

    @Override
    public void info(@NotNull String message, Object... args) {
        logger.log(Level.INFO, message, args);
    }

    @Override
    public void warning(@NotNull String message, Object... args) {
        logger.log(Level.WARNING, message, args);
    }

    @Override
    public void error(@NotNull String message, Object... args) {
        logger.log(Level.SEVERE, message, args);
    }

    @Override
    public void debug(@NotNull String message, Object... args) {
        if (verboseLogging) {
            logger.log(Level.INFO, "[DEBUG] " + message, args);
        }
    }

    @Override
    public long logDependencyLoadStart(@NotNull String dependency) {
        String[] parts = parseDependency(dependency);
        debug(t("开始加载依赖: {0}:{1}:{2}", "Loading dependency: {0}:{1}:{2}"),
            parts[0], parts[1], parts[2]);
        return System.currentTimeMillis();
    }

    @Override
    public void logDependencyLoadSuccess(@NotNull String dependency, long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        loadedDependencies.incrementAndGet();
        totalLoadTime.addAndGet(duration);
        String[] parts = parseDependency(dependency);
        info(t("✓ 依赖加载成功: {0}:{1}:{2} (耗时: {3}ms)", "✓ Dependency loaded: {0}:{1}:{2} ({3}ms)"),
            parts[0], parts[1], parts[2], duration);
    }

    @Override
    public void logDependencyLoadFailure(@NotNull String dependency, long startTime, @NotNull Throwable error) {
        long duration = System.currentTimeMillis() - startTime;
        failedDependencies.incrementAndGet();
        totalLoadTime.addAndGet(duration);
        String[] parts = parseDependency(dependency);
        warning(t("✗ 依赖加载失败: {0}:{1}:{2} (耗时: {3}ms) - {4}",
            "✗ Failed to load dependency: {0}:{1}:{2} ({3}ms) - {4}"),
            parts[0], parts[1], parts[2], duration, error.getMessage());
    }

    @Override
    public void logDependencyStats() {
        int loaded = loadedDependencies.get();
        int failed = failedDependencies.get();
        long totalTime = totalLoadTime.get();
        if (loaded > 0 || failed > 0) {
            info(t("依赖加载统计: 成功 {0}, 失败 {1}, 总耗时 {2}ms",
                "Dependency loading summary: {0} loaded, {1} failed, total time {2}ms"),
                loaded, failed, totalTime);
        }
    }

    @Override
    public void resetStats() {
        loadedDependencies.set(0);
        failedDependencies.set(0);
        totalLoadTime.set(0);
    }

    @Override
    public int getLoadedCount() {
        return loadedDependencies.get();
    }

    @Override
    public int getFailedCount() {
        return failedDependencies.get();
    }

    @Override
    public long getTotalLoadTime() {
        return totalLoadTime.get();
    }

    /**
     * 解析依赖坐标字符串
     *
     * @param dependency 格式: groupId:artifactId:version 或 groupId:artifactId:version:classifier
     * @return [groupId, artifactId, version]
     */
    private String[] parseDependency(String dependency) {
        try {
            String[] parts = dependency.split(":");
            if (parts.length >= 3) {
                return new String[]{parts[0], parts[1], parts[2]};
            }
        } catch (Exception e) {
            // 解析失败时返回原始字符串
        }
        return new String[]{dependency, "", ""};
    }
}
