package me.zhenxin.zmusic.dependencies;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * 依赖管理配置类（对象化版本）
 * 替代静态的 {@link DependencyConfig}，提供更灵活的配置管理
 *
 * <p>配置优先级：
 * <ol>
 *   <li>TOML 配置文件（如果提供）</li>
 *   <li>系统属性 (-Dzmusic.dependency.xxx)</li>
 *   <li>环境变量 (ZMUSIC_DEPENDENCY_XXX)</li>
 *   <li>默认值</li>
 * </ol>
 *
 * @author ZMusic Team
 * @since 4.0.0
 */
public class DependencyConfiguration {

    // 默认值常量
    private static final String DEFAULT_REPOSITORY = "https://maven.aliyun.com/repository/central";
    private static final String DEFAULT_LIBRARY_DIR = "libraries";
    private static final int DEFAULT_RETRY_COUNT = 3;
    private static final int DEFAULT_TIMEOUT = 30000; // 30 秒
    private static final boolean DEFAULT_VERBOSE = false;
    private static final boolean DEFAULT_PARALLEL = false;
    private static final int DEFAULT_PARALLEL_MAX = 3;
    private static final int DEFAULT_CLEANUP_INTERVAL = 24; // 24 小时
    private static final int DEFAULT_MAX_FILE_AGE = 168; // 7 天

    // 配置字段
    private String repository;
    private String libraryDirectory;
    private int retryCount;
    private int timeoutMillis;
    private boolean verboseLogging;
    private boolean parallelDownload;
    private int maxParallelDownloads;
    private int cleanupIntervalHours;
    private int maxFileAgeHours;

    /**
     * 创建默认配置实例
     */
    public DependencyConfiguration() {
        this.repository = loadProperty("zmusic.dependency.repository", DEFAULT_REPOSITORY);
        this.libraryDirectory = loadProperty("zmusic.dependency.library", DEFAULT_LIBRARY_DIR);
        this.retryCount = loadIntProperty("zmusic.dependency.retry", DEFAULT_RETRY_COUNT);
        this.timeoutMillis = loadIntProperty("zmusic.dependency.timeout", DEFAULT_TIMEOUT);
        this.verboseLogging = loadBoolProperty("zmusic.dependency.verbose", DEFAULT_VERBOSE);
        this.parallelDownload = loadBoolProperty("zmusic.dependency.parallel", DEFAULT_PARALLEL);
        this.maxParallelDownloads = loadIntProperty("zmusic.dependency.parallel.max", DEFAULT_PARALLEL_MAX);
        this.cleanupIntervalHours = loadIntProperty("zmusic.dependency.cleanup.interval", DEFAULT_CLEANUP_INTERVAL);
        this.maxFileAgeHours = loadIntProperty("zmusic.dependency.file.maxage", DEFAULT_MAX_FILE_AGE);
    }

    /**
     * 从 TOML 配置文件加载配置
     *
     * @param configFile TOML 配置文件
     * @return 配置实例
     */
    public static DependencyConfiguration fromToml(@NotNull File configFile) {
        DependencyConfiguration config = new DependencyConfiguration();

        if (!configFile.exists() || !configFile.isFile()) {
            return config; // 返回默认配置
        }

        try (InputStream input = new FileInputStream(configFile)) {
            // 注意：这里使用 Properties 作为简化实现
            // 在实际项目中，可以使用 Night Config 库解析 TOML
            Properties props = new Properties();
            props.load(input);

            config.repository = props.getProperty("dependency.repository", config.repository);
            config.libraryDirectory = props.getProperty("dependency.library", config.libraryDirectory);
            config.retryCount = parseIntProperty(props, "dependency.retry", config.retryCount);
            config.timeoutMillis = parseIntProperty(props, "dependency.timeout", config.timeoutMillis);
            config.verboseLogging = parseBoolProperty(props, "dependency.verbose", config.verboseLogging);
            config.parallelDownload = parseBoolProperty(props, "parallel.enabled", config.parallelDownload);
            config.maxParallelDownloads = parseIntProperty(props, "parallel.max-threads", config.maxParallelDownloads);
            config.cleanupIntervalHours = parseIntProperty(props, "cleanup.interval-hours", config.cleanupIntervalHours);
            config.maxFileAgeHours = parseIntProperty(props, "cleanup.file-max-age-hours", config.maxFileAgeHours);

        } catch (Exception e) {
            // 加载失败时使用默认配置
            System.err.println("Failed to load dependency configuration from " + configFile + ": " + e.getMessage());
        }

        return config;
    }

    /**
     * 从系统属性和环境变量加载配置值
     */
    private static String loadProperty(String key, String defaultValue) {
        // 首先尝试系统属性
        String value = System.getProperty(key);
        if (value != null && !value.trim().isEmpty()) {
            return value.trim();
        }

        // 然后尝试环境变量（将点号替换为下划线并转为大写）
        String envKey = key.replace('.', '_').toUpperCase();
        value = System.getenv(envKey);
        if (value != null && !value.trim().isEmpty()) {
            return value.trim();
        }

        return defaultValue;
    }

    private static int loadIntProperty(String key, int defaultValue) {
        String value = loadProperty(key, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static boolean loadBoolProperty(String key, boolean defaultValue) {
        String value = loadProperty(key, String.valueOf(defaultValue));
        return Boolean.parseBoolean(value);
    }

    private static int parseIntProperty(Properties props, String key, int defaultValue) {
        String value = props.getProperty(key);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static boolean parseBoolProperty(Properties props, String key, boolean defaultValue) {
        String value = props.getProperty(key);
        if (value == null) return defaultValue;
        return Boolean.parseBoolean(value.trim());
    }

    // ==================== Getters and Setters ====================

    @NotNull
    public String getRepository() {
        return repository;
    }

    public void setRepository(@NotNull String repository) {
        if (repository == null || repository.trim().isEmpty()) {
            throw new IllegalArgumentException("Repository URL cannot be null or empty");
        }
        this.repository = repository.trim();
    }

    @NotNull
    public String getLibraryDirectory() {
        return libraryDirectory;
    }

    public void setLibraryDirectory(@NotNull String libraryDirectory) {
        if (libraryDirectory == null || libraryDirectory.trim().isEmpty()) {
            throw new IllegalArgumentException("Library directory cannot be null or empty");
        }
        this.libraryDirectory = libraryDirectory.trim();
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        if (retryCount < 0) {
            throw new IllegalArgumentException("Retry count cannot be negative");
        }
        this.retryCount = retryCount;
    }

    public int getTimeoutMillis() {
        return timeoutMillis;
    }

    public void setTimeoutMillis(int timeoutMillis) {
        if (timeoutMillis <= 0) {
            throw new IllegalArgumentException("Timeout must be positive");
        }
        this.timeoutMillis = timeoutMillis;
    }

    public boolean isVerboseLogging() {
        return verboseLogging;
    }

    public void setVerboseLogging(boolean verboseLogging) {
        this.verboseLogging = verboseLogging;
    }

    public boolean isParallelDownload() {
        return parallelDownload;
    }

    public void setParallelDownload(boolean parallelDownload) {
        this.parallelDownload = parallelDownload;
    }

    public int getMaxParallelDownloads() {
        return maxParallelDownloads;
    }

    public void setMaxParallelDownloads(int maxParallelDownloads) {
        if (maxParallelDownloads <= 0) {
            throw new IllegalArgumentException("Max parallel downloads must be positive");
        }
        this.maxParallelDownloads = maxParallelDownloads;
    }

    public int getCleanupIntervalHours() {
        return cleanupIntervalHours;
    }

    public void setCleanupIntervalHours(int cleanupIntervalHours) {
        if (cleanupIntervalHours <= 0) {
            throw new IllegalArgumentException("Cleanup interval must be positive");
        }
        this.cleanupIntervalHours = cleanupIntervalHours;
    }

    public int getMaxFileAgeHours() {
        return maxFileAgeHours;
    }

    public void setMaxFileAgeHours(int maxFileAgeHours) {
        if (maxFileAgeHours <= 0) {
            throw new IllegalArgumentException("Max file age must be positive");
        }
        this.maxFileAgeHours = maxFileAgeHours;
    }

    // ==================== 实用方法 ====================

    /**
     * 验证配置是否有效
     *
     * @throws IllegalStateException 如果配置无效
     */
    public void validate() {
        if (repository == null || repository.trim().isEmpty()) {
            throw new IllegalStateException("Repository URL is required");
        }
        if (libraryDirectory == null || libraryDirectory.trim().isEmpty()) {
            throw new IllegalStateException("Library directory is required");
        }
        if (retryCount < 0) {
            throw new IllegalStateException("Retry count cannot be negative");
        }
        if (timeoutMillis <= 0) {
            throw new IllegalStateException("Timeout must be positive");
        }
        if (maxParallelDownloads <= 0) {
            throw new IllegalStateException("Max parallel downloads must be positive");
        }
    }

    /**
     * 创建配置的副本
     */
    public DependencyConfiguration copy() {
        DependencyConfiguration copy = new DependencyConfiguration();
        copy.repository = this.repository;
        copy.libraryDirectory = this.libraryDirectory;
        copy.retryCount = this.retryCount;
        copy.timeoutMillis = this.timeoutMillis;
        copy.verboseLogging = this.verboseLogging;
        copy.parallelDownload = this.parallelDownload;
        copy.maxParallelDownloads = this.maxParallelDownloads;
        copy.cleanupIntervalHours = this.cleanupIntervalHours;
        copy.maxFileAgeHours = this.maxFileAgeHours;
        return copy;
    }

    @Override
    public String toString() {
        return "DependencyConfiguration{" +
                "repository='" + repository + '\'' +
                ", libraryDirectory='" + libraryDirectory + '\'' +
                ", retryCount=" + retryCount +
                ", timeoutMillis=" + timeoutMillis +
                ", verboseLogging=" + verboseLogging +
                ", parallelDownload=" + parallelDownload +
                ", maxParallelDownloads=" + maxParallelDownloads +
                ", cleanupIntervalHours=" + cleanupIntervalHours +
                ", maxFileAgeHours=" + maxFileAgeHours +
                '}';
    }
}
