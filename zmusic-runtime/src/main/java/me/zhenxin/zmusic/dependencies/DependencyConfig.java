package me.zhenxin.zmusic.dependencies;

/**
 * 依赖管理配置类
 *
 * @author 真心
 * @since 2024
 */
public class DependencyConfig {

    // 默认Maven仓库地址
    public static final String DEFAULT_REPOSITORY = getProperty("zmusic.dependency.repository", "https://maven.aliyun.com/repository/central");

    // 默认库目录名称
    public static final String DEFAULT_LIBRARY_DIR = getProperty("zmusic.dependency.library", "libraries");

    // 依赖下载重试次数
    public static final int DEFAULT_RETRY_COUNT = getIntProperty("zmusic.dependency.retry", 3);

    // 依赖下载超时时间（毫秒）
    public static final int DEFAULT_TIMEOUT = getIntProperty("zmusic.dependency.timeout", 30000);

    // 是否启用详细日志
    public static final boolean VERBOSE_LOGGING = getBoolProperty("zmusic.dependency.verbose", false);

    // 是否启用并行下载
    public static final boolean PARALLEL_DOWNLOAD = getBoolProperty("zmusic.dependency.parallel", false);

    // 最大并行下载数
    public static final int MAX_PARALLEL_DOWNLOADS = getIntProperty("zmusic.dependency.parallel.max", 3);

    // 重定向文件清理间隔（小时）
    public static final int CLEANUP_INTERVAL_HOURS = getIntProperty("zmusic.dependency.cleanup.interval", 24);

    // 重定向文件最大保留时间（小时）
    public static final int MAX_FILE_AGE_HOURS = getIntProperty("zmusic.dependency.file.maxage", 168); // 7天

    /**
     * 获取系统属性，支持环境变量回退
     */
    private static String getProperty(String key, String defaultValue) {
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

    /**
     * 获取整数类型的系统属性
     */
    private static int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 获取布尔类型的系统属性
     */
    private static boolean getBoolProperty(String key, boolean defaultValue) {
        String value = getProperty(key, String.valueOf(defaultValue));
        return Boolean.parseBoolean(value);
    }
}