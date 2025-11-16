package me.zhenxin.zmusic.dependencies;

import me.zhenxin.zmusic.dependencies.common.CacheMetadata;
import me.zhenxin.zmusic.dependencies.common.RuntimeLogger;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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

    // 统计信息
    private final AtomicInteger cacheHits = new AtomicInteger(0);
    private final AtomicInteger cacheMisses = new AtomicInteger(0);

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
     * 使用 SHA-256 哈希值进行智能判断
     *
     * @param originalFile 原始文件
     * @param relocatedFile 重定向文件
     * @return true 表示需要重新生成
     */
    public boolean needsRelocate(File originalFile, File relocatedFile) {
        return needsRelocate(originalFile, relocatedFile, 0);
    }

    /**
     * 检查重定向文件是否需要重新生成（带重定位规则哈希）
     * 使用 SHA-256 哈希值进行智能判断
     *
     * @param originalFile   原始文件
     * @param relocatedFile  重定向文件
     * @param relocationHash 重定位规则的哈希值
     * @return true 表示需要重新生成
     */
    public boolean needsRelocate(File originalFile, File relocatedFile, int relocationHash) {
        // 1. 快速检查：重定向文件不存在或为空
        if (!relocatedFile.exists() || relocatedFile.length() == 0) {
            cacheMisses.incrementAndGet();
            RuntimeLogger.debug(t(
                    "缓存未命中: 重定向文件不存在或为空 - {0}",
                    "Cache miss: Relocated file does not exist or is empty - {0}"
            ), relocatedFile.getName());
            return true;
        }

        // 2. 快速检查：原文件不存在
        if (!originalFile.exists()) {
            cacheMisses.incrementAndGet();
            RuntimeLogger.debug(t(
                    "缓存未命中: 原始文件不存在 - {0}",
                    "Cache miss: Original file does not exist - {0}"
            ), originalFile.getName());
            return true;
        }

        // 3. 加载元数据文件
        File metadataFile = getMetadataFile(relocatedFile);
        CacheMetadata metadata = CacheMetadata.loadFromFile(metadataFile);

        // 4. 如果没有元数据，使用降级策略（时间戳比较）
        if (metadata == null) {
            cacheMisses.incrementAndGet();
            RuntimeLogger.debug(t(
                    "缓存未命中: 元数据文件不存在，降级使用时间戳比较 - {0}",
                    "Cache miss: Metadata file does not exist, fallback to timestamp comparison - {0}"
            ), relocatedFile.getName());
            return relocatedFile.lastModified() < originalFile.lastModified();
        }

        // 5. 使用 SHA-256 验证
        boolean matches = metadata.matches(originalFile, relocationHash);
        if (matches) {
            cacheHits.incrementAndGet();
            RuntimeLogger.debug(t(
                    "缓存命中: SHA-256 验证通过 - {0}",
                    "Cache hit: SHA-256 verification passed - {0}"
            ), relocatedFile.getName());
            return false;
        } else {
            cacheMisses.incrementAndGet();
            RuntimeLogger.debug(t(
                    "缓存未命中: SHA-256 验证失败 - {0}",
                    "Cache miss: SHA-256 verification failed - {0}"
            ), relocatedFile.getName());
            return true;
        }
    }

    /**
     * 保存重定向文件的元数据
     *
     * @param originalFile   原始文件
     * @param relocatedFile  重定向文件
     * @param relocationHash 重定位规则的哈希值
     */
    public void saveMetadata(File originalFile, File relocatedFile, int relocationHash) {
        try {
            CacheMetadata metadata = CacheMetadata.fromFile(originalFile, relocationHash);
            File metadataFile = getMetadataFile(relocatedFile);
            metadata.saveToFile(metadataFile);
            RuntimeLogger.debug(t(
                    "保存元数据: {0}",
                    "Saved metadata: {0}"
            ), metadataFile.getName());
        } catch (IOException e) {
            RuntimeLogger.warning(t(
                    "保存元数据失败: {0}",
                    "Failed to save metadata: {0}"
            ), e.getMessage());
        }
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
     * 获取元数据文件路径
     *
     * @param relocatedFile 重定向文件
     * @return 元数据文件
     */
    private File getMetadataFile(File relocatedFile) {
        String name = relocatedFile.getName();
        return new File(relocatedFile.getParentFile(), name + ".metadata");
    }

    /**
     * 获取缓存命中次数
     */
    public int getCacheHits() {
        return cacheHits.get();
    }

    /**
     * 获取缓存未命中次数
     */
    public int getCacheMisses() {
        return cacheMisses.get();
    }

    /**
     * 获取缓存命中率
     */
    public double getCacheHitRate() {
        int total = cacheHits.get() + cacheMisses.get();
        if (total == 0) {
            return 0.0;
        }
        return (double) cacheHits.get() / total * 100.0;
    }

    /**
     * 重置统计信息
     */
    public void resetStats() {
        cacheHits.set(0);
        cacheMisses.set(0);
    }

    /**
     * 打印缓存统计信息
     */
    public void printStats() {
        int hits = cacheHits.get();
        int misses = cacheMisses.get();
        int total = hits + misses;

        if (total == 0) {
            RuntimeLogger.info(t(
                    "缓存统计: 无数据",
                    "Cache statistics: No data"
            ));
            return;
        }

        double hitRate = getCacheHitRate();
        RuntimeLogger.info(t(
                "缓存统计: 命中 {0}/{1} ({2,number,#.##}%)",
                "Cache statistics: Hits {0}/{1} ({2,number,#.##}%)"
        ), hits, total, hitRate);
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