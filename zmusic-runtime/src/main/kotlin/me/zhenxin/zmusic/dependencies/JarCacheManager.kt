package me.zhenxin.zmusic.dependencies

import me.zhenxin.zmusic.dependencies.common.CacheMetadata
import me.zhenxin.zmusic.dependencies.common.PrimitiveIO.t
import me.zhenxin.zmusic.dependencies.common.RuntimeLogger
import java.io.File
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class JarCacheManager private constructor() {
    private val scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor { runnable ->
        Thread(runnable, "JarCacheManager").apply { isDaemon = true }
    }
    private val cacheHits = AtomicInteger(0)
    private val cacheMisses = AtomicInteger(0)

    @Volatile
    private var initialized = false

    @Synchronized
    fun initialize() {
        if (initialized) {
            return
        }
        val configuration = DependencyConfiguration.getGlobal()
        scheduler.scheduleAtFixedRate(
            this::cleanupExpiredFiles,
            configuration.cleanupIntervalHours.toLong(),
            configuration.cleanupIntervalHours.toLong(),
            TimeUnit.HOURS,
        )
        initialized = true
    }

    fun cleanupExpiredFiles() {
        RuntimeLogger.debug(t("开始清理过期的重定向 JAR 文件...", "Starting cleanup of expired relocated JAR files..."))
        val libraryDir = File(RuntimeEnv.ENV_DEPENDENCY.defaultLibrary)
        if (!libraryDir.exists() || !libraryDir.isDirectory) {
            return
        }
        cleanupDirectory(libraryDir)
    }

    fun needsRelocate(originalFile: File, relocatedFile: File): Boolean {
        return needsRelocate(originalFile, relocatedFile, 0)
    }

    fun needsRelocate(originalFile: File, relocatedFile: File, relocationHash: Int): Boolean {
        if (!relocatedFile.exists() || relocatedFile.length() == 0L) {
            cacheMisses.incrementAndGet()
            RuntimeLogger.debug(
                t("缓存未命中: 重定向文件不存在或为空 - {0}", "Cache miss: Relocated file does not exist or is empty - {0}"),
                relocatedFile.name,
            )
            return true
        }
        if (!originalFile.exists()) {
            cacheMisses.incrementAndGet()
            RuntimeLogger.debug(
                t("缓存未命中: 原始文件不存在 - {0}", "Cache miss: Original file does not exist - {0}"),
                originalFile.name,
            )
            return true
        }

        val metadataFile = metadataFile(relocatedFile)
        val metadata = CacheMetadata.loadFromFile(metadataFile)
        if (metadata == null) {
            cacheMisses.incrementAndGet()
            RuntimeLogger.debug(
                t(
                    "缓存未命中: 元数据文件不存在，降级使用时间戳比较 - {0}",
                    "Cache miss: Metadata file does not exist, fallback to timestamp comparison - {0}",
                ),
                relocatedFile.name,
            )
            return relocatedFile.lastModified() < originalFile.lastModified()
        }

        val matches = metadata.matches(originalFile, relocationHash)
        if (matches) {
            cacheHits.incrementAndGet()
            RuntimeLogger.debug(
                t("缓存命中: SHA-256 验证通过 - {0}", "Cache hit: SHA-256 verification passed - {0}"),
                relocatedFile.name,
            )
            return false
        }

        cacheMisses.incrementAndGet()
        RuntimeLogger.debug(
            t("缓存未命中: SHA-256 验证失败 - {0}", "Cache miss: SHA-256 verification failed - {0}"),
            relocatedFile.name,
        )
        return true
    }

    fun saveMetadata(originalFile: File, relocatedFile: File, relocationHash: Int) {
        try {
            CacheMetadata.fromFile(originalFile, relocationHash).saveToFile(metadataFile(relocatedFile))
            RuntimeLogger.debug(t("保存元数据: {0}", "Saved metadata: {0}"), metadataFile(relocatedFile).name)
        } catch (exception: IOException) {
            RuntimeLogger.warning(t("保存元数据失败: {0}", "Failed to save metadata: {0}"), exception.message)
        }
    }

    fun getRelocatedFile(originalFile: File, relocationHash: Int): File {
        val baseName = originalFile.name.substringBeforeLast('.', originalFile.name)
        return File(originalFile.parentFile, "${baseName}_r2_${kotlin.math.abs(relocationHash)}.jar")
    }

    fun getCacheHits(): Int {
        return cacheHits.get()
    }

    fun getCacheMisses(): Int {
        return cacheMisses.get()
    }

    fun getCacheHitRate(): Double {
        val total = cacheHits.get() + cacheMisses.get()
        return if (total == 0) 0.0 else cacheHits.get().toDouble() / total * 100.0
    }

    fun resetStats() {
        cacheHits.set(0)
        cacheMisses.set(0)
    }

    fun printStats() {
        val hits = cacheHits.get()
        val misses = cacheMisses.get()
        val total = hits + misses
        if (total == 0) {
            RuntimeLogger.info(t("缓存统计: 无数据", "Cache statistics: No data"))
            return
        }
        RuntimeLogger.info(
            t("缓存统计: 命中 {0}/{1} ({2,number,#.##}%)", "Cache statistics: Hits {0}/{1} ({2,number,#.##}%)"),
            hits,
            total,
            getCacheHitRate(),
        )
    }

    fun shutdown() {
        scheduler.shutdown()
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow()
            }
        } catch (_: InterruptedException) {
            scheduler.shutdownNow()
            Thread.currentThread().interrupt()
        }
    }

    private fun cleanupDirectory(directory: File) {
        val files = directory.listFiles() ?: return
        val maxAgeMs = DependencyConfiguration.getGlobal().maxFileAgeHours * 60L * 60L * 1000L
        val currentTime = System.currentTimeMillis()
        var cleanedCount = 0
        for (file in files) {
            if (file.isDirectory) {
                cleanupDirectory(file)
            } else if (isRelocatedJar(file) && currentTime - file.lastModified() > maxAgeMs) {
                if (file.delete()) {
                    cleanedCount++
                    RuntimeLogger.debug(t("清理过期文件: {0}", "Cleaned up expired file: {0}"), file.name)
                }
            }
        }
        if (cleanedCount > 0) {
            RuntimeLogger.debug(
                t("在 {0} 中清理了 {1} 个过期文件", "Cleaned up {1} expired files in {0}"),
                directory.path,
                cleanedCount,
            )
        }
    }

    private fun isRelocatedJar(file: File): Boolean {
        return file.name.endsWith(".jar") && file.name.contains("_r2_")
    }

    private fun metadataFile(relocatedFile: File): File {
        return File(relocatedFile.parentFile, relocatedFile.name + ".metadata")
    }

    companion object {
        private val INSTANCE = JarCacheManager()

        @JvmStatic
        fun getInstance(): JarCacheManager {
            return INSTANCE
        }
    }
}
