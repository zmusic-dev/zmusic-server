package me.zhenxin.zmusic.dependencies.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Java bootstrap 阶段的最小共享上下文。
 * 同时承载日志、配置状态和 Java 版本探测，避免继续扩散 Java 辅助类。
 */
public final class BootstrapContext {

    private static final Logger LOGGER = Logger.getLogger("ZMusic");
    private static final boolean CHINESE = detectChineseEnvironment();
    private static final String DEFAULT_REPOSITORY = "https://maven.aliyun.com/repository/central";
    private static final String DEFAULT_LIBRARY_DIRECTORY = "libraries";
    private static final String JAVA_VERSION_STRING = System.getProperty("java.version");
    private static final int JAVA_VERSION = parseJavaVersion(JAVA_VERSION_STRING);

    private static volatile String repository = loadProperty("zmusic.dependency.repository", DEFAULT_REPOSITORY);
    private static volatile String defaultLibrary = loadProperty("zmusic.dependency.library", DEFAULT_LIBRARY_DIRECTORY);

    private BootstrapContext() {
    }

    public static String getRepository() { return repository; }

    public static void setRepository(String repositoryUrl) { repository = repositoryUrl; }

    public static String getDefaultLibrary() { return defaultLibrary; }

    public static void setDefaultLibrary(String libraryDirectory) { defaultLibrary = libraryDirectory; }

    public static void info(String message, Object... args) { LOGGER.log(Level.INFO, message, args); }

    public static void warning(String message, Object... args) { LOGGER.log(Level.WARNING, message, args); }

    public static void debug(String message, Object... args) {
        if (isVerbose()) {
            LOGGER.log(Level.INFO, "[DEBUG] " + message, args);
        }
    }

    public static String t(String zh, String en) { return CHINESE ? zh : en; }

    public static String stackTraceOf(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        return stringWriter.toString();
    }

    public static String getJavaDebugInfo() {
        return "Java Version: " + JAVA_VERSION_STRING
                + " (Major: " + JAVA_VERSION + ")"
                + " | Java 8: " + (JAVA_VERSION == 8)
                + " | Java 9+: " + (JAVA_VERSION >= 9);
    }

    private static boolean detectChineseEnvironment() {
        try {
            return Locale.getDefault().toLanguageTag().startsWith("zh");
        } catch (Throwable ignored) {
            return true;
        }
    }

    private static boolean isVerbose() {
        return Boolean.parseBoolean(loadProperty("zmusic.dependency.verbose", "false"));
    }

    private static String loadProperty(String key, String defaultValue) {
        String systemProperty = System.getProperty(key);
        if (systemProperty != null && !systemProperty.trim().isEmpty()) {
            return systemProperty.trim();
        }
        String envKey = key.replace('.', '_').toUpperCase();
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.trim().isEmpty()) {
            return envValue.trim();
        }
        return defaultValue;
    }

    private static int parseJavaVersion(String version) {
        try {
            if (version.startsWith("1.")) {
                String[] parts = version.split("\\.");
                if (parts.length >= 2) {
                    return Integer.parseInt(parts[1]);
                }
            }
            String[] parts = version.split("\\.");
            if (parts.length > 0) {
                return Integer.parseInt(parts[0].split("-")[0]);
            }
        } catch (Exception ignored) {
            warning(
                    t("无法解析 Java 版本: " + version + "，默认使用版本 8", "Failed to parse Java version: " + version + ", defaulting to version 8")
            );
        }
        return 8;
    }
}
