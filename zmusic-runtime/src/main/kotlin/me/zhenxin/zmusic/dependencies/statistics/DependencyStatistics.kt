package me.zhenxin.zmusic.dependencies.statistics

import me.zhenxin.zmusic.dependencies.common.RuntimeLogger
import me.zhenxin.zmusic.dependencies.common.PrimitiveIO.t
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * 依赖加载统计信息
 * 提供详细的加载统计、性能分析和报告生成
 */
class DependencyStatistics private constructor() {

    private val totalDependencies = AtomicInteger(0)
    private val successfulLoads = AtomicInteger(0)
    private val failedLoads = AtomicInteger(0)
    private val totalDownloadBytes = AtomicLong(0)
    private val totalLoadTimeMs = AtomicLong(0)

    private val loadRecords = ConcurrentHashMap<String, LoadRecord>()
    private val failureMessages = CopyOnWriteArrayList<String>()
    private val timings = ConcurrentHashMap<String, Long>()

    fun recordLoadStart(coordinate: String) {
        totalDependencies.incrementAndGet()
        timings[coordinate] = System.currentTimeMillis()
    }

    fun recordLoadSuccess(coordinate: String, fileSize: Long) {
        successfulLoads.incrementAndGet()
        totalDownloadBytes.addAndGet(fileSize)

        val startTime = timings[coordinate] ?: System.currentTimeMillis()
        val duration = System.currentTimeMillis() - startTime
        totalLoadTimeMs.addAndGet(duration)

        loadRecords[coordinate] = LoadRecord(coordinate, true, duration, fileSize, null)
        timings.remove(coordinate)
    }

    fun recordLoadFailure(coordinate: String, error: String) {
        failedLoads.incrementAndGet()

        val startTime = timings[coordinate] ?: System.currentTimeMillis()
        val duration = System.currentTimeMillis() - startTime

        loadRecords[coordinate] = LoadRecord(coordinate, false, duration, 0, error)
        failureMessages.add("$coordinate: $error")
        timings.remove(coordinate)
    }

    fun recordTiming(metric: String, value: Long) {
        timings[metric] = value
    }

    fun getTotalDependencies(): Int {
        return totalDependencies.get()
    }

    fun getSuccessfulLoads(): Int {
        return successfulLoads.get()
    }

    fun getFailedLoads(): Int {
        return failedLoads.get()
    }

    fun getTotalDownloadBytes(): Long {
        return totalDownloadBytes.get()
    }

    fun getTotalLoadTimeMs(): Long {
        return totalLoadTimeMs.get()
    }

    fun getSuccessRate(): Double {
        val total = totalDependencies.get()
        if (total == 0) {
            return 0.0
        }
        return successfulLoads.get().toDouble() / total * 100.0
    }

    fun getAverageLoadTimeMs(): Long {
        val successful = successfulLoads.get()
        if (successful == 0) {
            return 0
        }
        return totalLoadTimeMs.get() / successful
    }

    fun getSlowestLoad(): LoadRecord? {
        return loadRecords.values
            .asSequence()
            .filter { it.isSuccess() }
            .maxByOrNull { it.getDurationMs() }
    }

    fun getFastestLoad(): LoadRecord? {
        return loadRecords.values
            .asSequence()
            .filter { it.isSuccess() }
            .minByOrNull { it.getDurationMs() }
    }

    fun reset() {
        totalDependencies.set(0)
        successfulLoads.set(0)
        failedLoads.set(0)
        totalDownloadBytes.set(0)
        totalLoadTimeMs.set(0)
        loadRecords.clear()
        failureMessages.clear()
        timings.clear()
    }

    fun printSummary() {
        RuntimeLogger.info(
            t(
                "依赖加载统计: 总计 {0}, 成功 {1}, 失败 {2} ({3,number,#.##}%)",
                "Dependency load statistics: Total {0}, Success {1}, Failed {2} ({3,number,#.##}%)",
            ),
            totalDependencies.get(),
            successfulLoads.get(),
            failedLoads.get(),
            getSuccessRate(),
        )

        RuntimeLogger.info(
            t(
                "总下载大小: {0} MB, 总耗时: {1} ms, 平均耗时: {2} ms",
                "Total download size: {0} MB, Total time: {1} ms, Average time: {2} ms",
            ),
            totalDownloadBytes.get() / 1024 / 1024,
            totalLoadTimeMs.get(),
            getAverageLoadTimeMs(),
        )
    }

    @Throws(IOException::class)
    fun generateReport(file: File) {
        file.bufferedWriter().use { writer ->
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

            writer.write("Dependency Loading Report\n")
            writer.write("=========================\n")
            writer.write("Generated at: ${dateFormat.format(Date())}\n\n")

            writer.write("Summary Statistics\n")
            writer.write("------------------\n")
            writer.write("Total dependencies: ${totalDependencies.get()}\n")
            writer.write("Successful loads: ${successfulLoads.get()}\n")
            writer.write("Failed loads: ${failedLoads.get()}\n")
            writer.write(String.format("Success rate: %.2f%%\n", getSuccessRate()))
            writer.write(String.format("Total download size: %.2f MB\n", totalDownloadBytes.get() / 1024.0 / 1024.0))
            writer.write("Total load time: ${totalLoadTimeMs.get()} ms\n")
            writer.write("Average load time: ${getAverageLoadTimeMs()} ms\n")

            val slowest = getSlowestLoad()
            val fastest = getFastestLoad()
            if (slowest != null && fastest != null) {
                writer.write("\nPerformance Analysis\n")
                writer.write("--------------------\n")
                writer.write(
                    String.format(
                        "Slowest load: %s (%d ms, %.2f KB)\n",
                        slowest.getCoordinate(),
                        slowest.getDurationMs(),
                        slowest.getFileSize() / 1024.0,
                    )
                )
                writer.write(
                    String.format(
                        "Fastest load: %s (%d ms, %.2f KB)\n",
                        fastest.getCoordinate(),
                        fastest.getDurationMs(),
                        fastest.getFileSize() / 1024.0,
                    )
                )
            }

            writer.write("\nSuccessful Loads\n")
            writer.write("----------------\n")
            loadRecords.values
                .asSequence()
                .filter { it.isSuccess() }
                .sortedByDescending { it.getDurationMs() }
                .forEach { record ->
                    writer.write(
                        String.format(
                            "  %s - %d ms (%.2f KB)\n",
                            record.getCoordinate(),
                            record.getDurationMs(),
                            record.getFileSize() / 1024.0,
                        )
                    )
                }

            if (failureMessages.isNotEmpty()) {
                writer.write("\nFailed Loads\n")
                writer.write("------------\n")
                for (message in failureMessages) {
                    writer.write("  $message\n")
                }
            }

            if (timings.isNotEmpty()) {
                writer.write("\nAdditional Timings\n")
                writer.write("------------------\n")
                for ((metric, value) in timings) {
                    writer.write("  $metric: $value ms\n")
                }
            }
        }
    }

    class LoadRecord(
        coordinate: String,
        success: Boolean,
        durationMs: Long,
        fileSize: Long,
        error: String?,
    ) {

        private val coordinate = coordinate
        private val success = success
        private val durationMs = durationMs
        private val fileSize = fileSize
        private val error = error

        fun getCoordinate(): String {
            return coordinate
        }

        fun isSuccess(): Boolean {
            return success
        }

        fun getDurationMs(): Long {
            return durationMs
        }

        fun getFileSize(): Long {
            return fileSize
        }

        fun getError(): String? {
            return error
        }
    }

    companion object {
        private val INSTANCE = DependencyStatistics()

        @JvmStatic
        fun getInstance(): DependencyStatistics {
            return INSTANCE
        }
    }
}
