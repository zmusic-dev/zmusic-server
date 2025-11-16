package me.zhenxin.zmusic.dependencies.statistics;

import me.zhenxin.zmusic.dependencies.common.RuntimeLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static me.zhenxin.zmusic.dependencies.common.PrimitiveIO.t;

/**
 * 依赖加载统计信息
 * 提供详细的加载统计、性能分析和报告生成
 *
 * @author ZMusic Team
 * @since 4.0.0
 */
public class DependencyStatistics {

    private static final DependencyStatistics INSTANCE = new DependencyStatistics();

    // 基础统计
    private final AtomicInteger totalDependencies = new AtomicInteger(0);
    private final AtomicInteger successfulLoads = new AtomicInteger(0);
    private final AtomicInteger failedLoads = new AtomicInteger(0);
    private final AtomicLong totalDownloadBytes = new AtomicLong(0);
    private final AtomicLong totalLoadTimeMs = new AtomicLong(0);

    // 详细记录
    private final Map<String, LoadRecord> loadRecords = new ConcurrentHashMap<>();
    private final List<String> failureMessages = new ArrayList<>();

    // 性能分析
    private final Map<String, Long> timings = new ConcurrentHashMap<>();

    private DependencyStatistics() {
    }

    @NotNull
    public static DependencyStatistics getInstance() {
        return INSTANCE;
    }

    /**
     * 记录依赖加载开始
     *
     * @param coordinate Maven 坐标
     */
    public void recordLoadStart(@NotNull String coordinate) {
        totalDependencies.incrementAndGet();
        timings.put(coordinate, System.currentTimeMillis());
    }

    /**
     * 记录依赖加载成功
     *
     * @param coordinate Maven 坐标
     * @param fileSize   文件大小（字节）
     */
    public void recordLoadSuccess(@NotNull String coordinate, long fileSize) {
        successfulLoads.incrementAndGet();
        totalDownloadBytes.addAndGet(fileSize);

        long startTime = timings.getOrDefault(coordinate, System.currentTimeMillis());
        long duration = System.currentTimeMillis() - startTime;
        totalLoadTimeMs.addAndGet(duration);

        loadRecords.put(coordinate, new LoadRecord(coordinate, true, duration, fileSize, null));
        timings.remove(coordinate);
    }

    /**
     * 记录依赖加载失败
     *
     * @param coordinate Maven 坐标
     * @param error      错误消息
     */
    public void recordLoadFailure(@NotNull String coordinate, @NotNull String error) {
        failedLoads.incrementAndGet();

        long startTime = timings.getOrDefault(coordinate, System.currentTimeMillis());
        long duration = System.currentTimeMillis() - startTime;

        loadRecords.put(coordinate, new LoadRecord(coordinate, false, duration, 0, error));
        failureMessages.add(coordinate + ": " + error);
        timings.remove(coordinate);
    }

    /**
     * 记录性能指标
     *
     * @param metric 指标名称
     * @param value  指标值（毫秒）
     */
    public void recordTiming(@NotNull String metric, long value) {
        timings.put(metric, value);
    }

    /**
     * 获取总依赖数
     */
    public int getTotalDependencies() {
        return totalDependencies.get();
    }

    /**
     * 获取成功加载数
     */
    public int getSuccessfulLoads() {
        return successfulLoads.get();
    }

    /**
     * 获取失败加载数
     */
    public int getFailedLoads() {
        return failedLoads.get();
    }

    /**
     * 获取总下载字节数
     */
    public long getTotalDownloadBytes() {
        return totalDownloadBytes.get();
    }

    /**
     * 获取总加载时间（毫秒）
     */
    public long getTotalLoadTimeMs() {
        return totalLoadTimeMs.get();
    }

    /**
     * 获取成功率
     */
    public double getSuccessRate() {
        int total = totalDependencies.get();
        if (total == 0) {
            return 0.0;
        }
        return (double) successfulLoads.get() / total * 100.0;
    }

    /**
     * 获取平均加载时间（毫秒）
     */
    public long getAverageLoadTimeMs() {
        int successful = successfulLoads.get();
        if (successful == 0) {
            return 0;
        }
        return totalLoadTimeMs.get() / successful;
    }

    /**
     * 获取最慢的依赖加载记录
     */
    @Nullable
    public LoadRecord getSlowestLoad() {
        return loadRecords.values().stream()
                .filter(LoadRecord::isSuccess)
                .max((a, b) -> Long.compare(a.getDurationMs(), b.getDurationMs()))
                .orElse(null);
    }

    /**
     * 获取最快的依赖加载记录
     */
    @Nullable
    public LoadRecord getFastestLoad() {
        return loadRecords.values().stream()
                .filter(LoadRecord::isSuccess)
                .min((a, b) -> Long.compare(a.getDurationMs(), b.getDurationMs()))
                .orElse(null);
    }

    /**
     * 重置统计信息
     */
    public void reset() {
        totalDependencies.set(0);
        successfulLoads.set(0);
        failedLoads.set(0);
        totalDownloadBytes.set(0);
        totalLoadTimeMs.set(0);
        loadRecords.clear();
        failureMessages.clear();
        timings.clear();
    }

    /**
     * 打印统计摘要
     */
    public void printSummary() {
        RuntimeLogger.info(t(
                "依赖加载统计: 总计 {0}, 成功 {1}, 失败 {2} ({3,number,#.##}%)",
                "Dependency load statistics: Total {0}, Success {1}, Failed {2} ({3,number,#.##}%)"
        ), totalDependencies.get(), successfulLoads.get(), failedLoads.get(), getSuccessRate());

        RuntimeLogger.info(t(
                "总下载大小: {0} MB, 总耗时: {1} ms, 平均耗时: {2} ms",
                "Total download size: {0} MB, Total time: {1} ms, Average time: {2} ms"
        ), totalDownloadBytes.get() / 1024 / 1024, totalLoadTimeMs.get(), getAverageLoadTimeMs());
    }

    /**
     * 生成详细报告
     *
     * @param file 输出文件
     * @throws IOException 如果写入失败
     */
    public void generateReport(@NotNull File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            // 标题
            writer.write("Dependency Loading Report\n");
            writer.write("=========================\n");
            writer.write("Generated at: " + dateFormat.format(new Date()) + "\n\n");

            // 摘要统计
            writer.write("Summary Statistics\n");
            writer.write("------------------\n");
            writer.write(String.format("Total dependencies: %d\n", totalDependencies.get()));
            writer.write(String.format("Successful loads: %d\n", successfulLoads.get()));
            writer.write(String.format("Failed loads: %d\n", failedLoads.get()));
            writer.write(String.format("Success rate: %.2f%%\n", getSuccessRate()));
            writer.write(String.format("Total download size: %.2f MB\n", totalDownloadBytes.get() / 1024.0 / 1024.0));
            writer.write(String.format("Total load time: %d ms\n", totalLoadTimeMs.get()));
            writer.write(String.format("Average load time: %d ms\n", getAverageLoadTimeMs()));

            // 性能分析
            LoadRecord slowest = getSlowestLoad();
            LoadRecord fastest = getFastestLoad();

            if (slowest != null && fastest != null) {
                writer.write("\nPerformance Analysis\n");
                writer.write("--------------------\n");
                writer.write(String.format("Slowest load: %s (%d ms, %.2f KB)\n",
                        slowest.getCoordinate(), slowest.getDurationMs(), slowest.getFileSize() / 1024.0));
                writer.write(String.format("Fastest load: %s (%d ms, %.2f KB)\n",
                        fastest.getCoordinate(), fastest.getDurationMs(), fastest.getFileSize() / 1024.0));
            }

            // 成功加载列表
            writer.write("\nSuccessful Loads\n");
            writer.write("----------------\n");
            loadRecords.values().stream()
                    .filter(LoadRecord::isSuccess)
                    .sorted((a, b) -> Long.compare(b.getDurationMs(), a.getDurationMs()))
                    .forEach(record -> {
                        try {
                            writer.write(String.format("  %s - %d ms (%.2f KB)\n",
                                    record.getCoordinate(), record.getDurationMs(), record.getFileSize() / 1024.0));
                        } catch (IOException e) {
                            // Ignore
                        }
                    });

            // 失败加载列表
            if (!failureMessages.isEmpty()) {
                writer.write("\nFailed Loads\n");
                writer.write("------------\n");
                for (String message : failureMessages) {
                    writer.write("  " + message + "\n");
                }
            }

            // 性能指标
            if (!timings.isEmpty()) {
                writer.write("\nAdditional Timings\n");
                writer.write("------------------\n");
                for (Map.Entry<String, Long> entry : timings.entrySet()) {
                    writer.write(String.format("  %s: %d ms\n", entry.getKey(), entry.getValue()));
                }
            }
        }
    }

    /**
     * 加载记录
     */
    public static class LoadRecord {
        private final String coordinate;
        private final boolean success;
        private final long durationMs;
        private final long fileSize;
        private final String error;

        public LoadRecord(@NotNull String coordinate, boolean success, long durationMs,
                          long fileSize, @Nullable String error) {
            this.coordinate = coordinate;
            this.success = success;
            this.durationMs = durationMs;
            this.fileSize = fileSize;
            this.error = error;
        }

        @NotNull
        public String getCoordinate() {
            return coordinate;
        }

        public boolean isSuccess() {
            return success;
        }

        public long getDurationMs() {
            return durationMs;
        }

        public long getFileSize() {
            return fileSize;
        }

        @Nullable
        public String getError() {
            return error;
        }
    }
}
