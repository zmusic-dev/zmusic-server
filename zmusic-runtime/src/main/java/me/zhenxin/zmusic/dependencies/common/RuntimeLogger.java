package me.zhenxin.zmusic.dependencies.common;

import me.zhenxin.zmusic.dependencies.DependencyConfig;

import java.util.logging.Level;

import static me.zhenxin.zmusic.dependencies.common.PrimitiveIO.t;

/**
 * @author zhenxin
 */
public class RuntimeLogger {

    /**
     * 日志对象
     */
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger("ZMusic");

    /**
     * 依赖加载统计
     */
    private static int loadedDependencies = 0;
    private static int failedDependencies = 0;
    private static long totalLoadTime = 0;

    /**
     * 输出 Info 日志
     *
     * @param message 日志内容
     */
    public static void info(String message, Object... args) {
        LOGGER.log(Level.INFO, message, args);
    }

    /**
     * 输出 Warning 日志
     *
     * @param message 日志内容
     */
    public static void warning(String message, Object... args) {
        LOGGER.log(Level.WARNING, message, args);
    }

    /**
     * 输出 Error 日志
     *
     * @param message 日志内容
     */
    public static void error(String message, Object... args) {
        LOGGER.log(Level.SEVERE, message, args);
    }

    /**
     * 输出调试日志（仅在详细模式下）
     *
     * @param message 日志内容
     */
    public static void debug(String message, Object... args) {
        if (DependencyConfig.VERBOSE_LOGGING) {
            LOGGER.log(Level.INFO, "[DEBUG] " + message, args);
        }
    }

    /**
     * 记录依赖加载开始
     */
    public static long logDependencyLoadStart(String dependency) {
        String[] parts = parseDependency(dependency);
        debug(t("开始加载依赖: {0}:{1}:{2}", "Loading dependency: {0}:{1}:{2}"), parts[0], parts[1], parts[2]);
        return System.currentTimeMillis();
    }

    /**
     * 记录依赖加载成功
     */
    public static void logDependencyLoadSuccess(String dependency, long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        loadedDependencies++;
        totalLoadTime += duration;
        String[] parts = parseDependency(dependency);
        info(t("✓ 依赖加载成功: {0}:{1}:{2} (耗时: {3}ms)", "✓ Dependency loaded: {0}:{1}:{2} ({3}ms)"), parts[0], parts[1], parts[2], duration);
    }

    /**
     * 记录依赖加载失败
     */
    public static void logDependencyLoadFailure(String dependency, long startTime, Throwable error) {
        long duration = System.currentTimeMillis() - startTime;
        failedDependencies++;
        totalLoadTime += duration;
        String[] parts = parseDependency(dependency);
        warning(t("✗ 依赖加载失败: {0}:{1}:{2} (耗时: {3}ms) - {4}", "✗ Failed to load dependency: {0}:{1}:{2} ({3}ms) - {4}"), parts[0], parts[1], parts[2], duration, error.getMessage());
    }

    /**
     * 输出依赖加载统计信息
     */
    public static void logDependencyStats() {
        if (loadedDependencies > 0 || failedDependencies > 0) {
            info(t("依赖加载统计: 成功 {0}, 失败 {1}, 总耗时 {2}ms", "Dependency loading summary: {0} loaded, {1} failed, total time {2}ms"),
                    loadedDependencies, failedDependencies, totalLoadTime);
        }
    }

    /**
     * 重置统计信息
     */
    public static void resetStats() {
        loadedDependencies = 0;
        failedDependencies = 0;
        totalLoadTime = 0;
    }

    /**
     * 解析依赖坐标字符串
     *
     * @param dependency 格式: groupId:artifactId:version 或 groupId:artifactId:version:classifier
     * @return [groupId, artifactId, version]
     */
    private static String[] parseDependency(String dependency) {
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
