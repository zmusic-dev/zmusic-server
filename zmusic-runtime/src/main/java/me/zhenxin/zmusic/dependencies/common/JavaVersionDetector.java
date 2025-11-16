package me.zhenxin.zmusic.dependencies.common;

/**
 * Java 版本检测工具类
 * 用于在运行时检测 Java 版本，以便选择合适的实现策略
 *
 * @author ZMusic Team
 * @since 4.0.0
 */
public class JavaVersionDetector {

    private static final int JAVA_VERSION;
    private static final String JAVA_VERSION_STRING;

    static {
        String version = System.getProperty("java.version");
        JAVA_VERSION_STRING = version;
        JAVA_VERSION = parseVersion(version);
    }

    /**
     * 解析 Java 版本号
     * 支持的格式：
     * - Java 8: "1.8.0_XXX"
     * - Java 9+: "9.X.X", "11.X.X", "17.X.X", "21.X.X"
     *
     * @param version Java 版本字符串
     * @return 主版本号 (8, 9, 11, 17, 21 等)
     */
    private static int parseVersion(String version) {
        try {
            // Java 8 及以下: 1.8.0_XXX -> 8
            if (version.startsWith("1.")) {
                String[] parts = version.split("\\.");
                if (parts.length >= 2) {
                    return Integer.parseInt(parts[1]);
                }
            }
            // Java 9+: 直接取第一段
            String[] parts = version.split("\\.");
            if (parts.length > 0) {
                // 处理带有 "-" 的情况，如 "11.0.1-internal"
                String majorVersion = parts[0].split("-")[0];
                return Integer.parseInt(majorVersion);
            }
        } catch (Exception e) {
            RuntimeLogger.warning(PrimitiveIO.t(
                    "无法解析 Java 版本: " + version + "，默认使用版本 8",
                    "Failed to parse Java version: " + version + ", defaulting to version 8"
            ));
        }
        // 默认返回 8（最保守的版本）
        return 8;
    }

    /**
     * 获取 Java 主版本号
     *
     * @return Java 主版本号 (8, 11, 17, 21 等)
     */
    public static int getJavaVersion() {
        return JAVA_VERSION;
    }

    /**
     * 获取完整的 Java 版本字符串
     *
     * @return Java 版本字符串
     */
    public static String getJavaVersionString() {
        return JAVA_VERSION_STRING;
    }

    /**
     * 检查是否为 Java 8
     */
    public static boolean isJava8() {
        return JAVA_VERSION == 8;
    }

    /**
     * 检查是否为 Java 9-11
     */
    public static boolean isJava9To11() {
        return JAVA_VERSION >= 9 && JAVA_VERSION <= 11;
    }

    /**
     * 检查是否为 Java 12+
     */
    public static boolean isJava12Plus() {
        return JAVA_VERSION >= 12;
    }

    /**
     * 检查是否为 Java 9+
     */
    public static boolean isJava9Plus() {
        return JAVA_VERSION >= 9;
    }

    /**
     * 获取版本信息用于调试
     */
    public static String getDebugInfo() {
        return "Java Version: " + JAVA_VERSION_STRING +
                " (Major: " + JAVA_VERSION + ")" +
                " | Java 8: " + isJava8() +
                " | Java 9+: " + isJava9Plus();
    }
}
