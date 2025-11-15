package me.zhenxin.zmusic.dependencies;

import me.zhenxin.zmusic.dependencies.common.RuntimeLogger;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static me.zhenxin.zmusic.dependencies.common.PrimitiveIO.t;

/**
 * JAR缓存管理器，负责清理过期的重定向文件
 *
 * @author 真心
 * @since 2024
 */
public class JarCacheManager {

    private static final JarCacheManager INSTANCE = new JarCacheManager();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "JarCacheManager");
        t.setDaemon(true);
        return t;
    });

    private volatile boolean initialized = false;

    private JarCacheManager() {
    }

    public static JarCacheManager getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化缓存管理器，开始定期清理任务
     */
    public synchronized void initialize() {
        if (initialized) {
            return;
        }

        // 启动定期清理任务
        scheduler.scheduleAtFixedRate(
                this::cleanupExpiredFiles,
                DependencyConfig.CLEANUP_INTERVAL_HOURS,
                DependencyConfig.CLEANUP_INTERVAL_HOURS,
                TimeUnit.HOURS
        );

        initialized = true;
    }

    /**
     * 清理过期的重定向文件
     */
    public void cleanupExpiredFiles() {
        RuntimeLogger.debug(t(
                "开始清理过期的重定向 JAR 文件...",
                "Starting cleanup of expired relocated JAR files..."
        ));

        String libraryPath = RuntimeEnv.ENV_DEPENDENCY.getDefaultLibrary();
        File libraryDir = new File(libraryPath);

        if (!libraryDir.exists() || !libraryDir.isDirectory()) {
            return;
        }

        cleanupDirectory(libraryDir);
    }

    /**
     * 递归清理目录中的过期重定向文件
     */
    private void cleanupDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        long maxAgeMs = DependencyConfig.MAX_FILE_AGE_HOURS * 60 * 60 * 1000L;
        long currentTime = System.currentTimeMillis();
        int cleanedCount = 0;

        for (File file : files) {
            if (file.isDirectory()) {
                cleanupDirectory(file);
            } else if (isRelocatedJar(file)) {
                // 检查文件是否过期
                if (currentTime - file.lastModified() > maxAgeMs) {
                    if (file.delete()) {
                        cleanedCount++;
                        RuntimeLogger.debug(t(
                                "清理过期文件: {0}",
                                "Cleaned up expired file: {0}"
                        ), file.getName());
                    }
                }
            }
        }

        if (cleanedCount > 0) {
            RuntimeLogger.debug(t(
                    "在 {0} 中清理了 {1} 个过期文件",
                    "Cleaned up {1} expired files in {0}"
            ), directory.getPath(), cleanedCount);
        }
    }

    /**
     * 检查文件是否为重定向的JAR文件
     */
    private boolean isRelocatedJar(File file) {
        String name = file.getName();
        return name.endsWith(".jar") && name.contains("_r2_");
    }

    /**
     * 检查重定向文件是否需要重新生成
     */
    public boolean needsRelocate(File originalFile, File relocatedFile) {
        if (!relocatedFile.exists() || relocatedFile.length() == 0) {
            return true;
        }

        // 如果原文件更新，需要重新生成
        if (originalFile.exists() && relocatedFile.lastModified() < originalFile.lastModified()) {
            return true;
        }

        return false;
    }

    /**
     * 生成重定向文件的名称
     */
    public File getRelocatedFile(File originalFile, int relocationHash) {
        String name = originalFile.getName();
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex > 0) {
            name = name.substring(0, dotIndex);
        }

        return new File(originalFile.getParentFile(), name + "_r2_" + Math.abs(relocationHash) + ".jar");
    }

    /**
     * 关闭缓存管理器
     */
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}