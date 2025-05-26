package me.zhenxin.zmusic.dependencies.common;

import java.util.logging.Level;

/**
 * @author zhenxin
 */
public class RuntimeLogger {

    /**
     * 日志对象
     */
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger("ZMusic");

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
}
