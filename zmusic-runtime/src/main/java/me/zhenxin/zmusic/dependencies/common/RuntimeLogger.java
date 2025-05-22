package me.zhenxin.zmusic.dependencies.common;

import java.util.logging.Level;

public class RuntimeLogger {

    /**
     * 日志对象
     */
    private static java.util.logging.Logger logger = java.util.logging.Logger.getLogger("ZMusic");

    /**
     * 输出 Info 日志
     *
     * @param message 日志内容
     */
    public static void info(String message, Object... args) {
        logger.log(Level.INFO, message, args);
    }

    /**
     * 输出 Warning 日志
     *
     * @param message 日志内容
     */
    public static void warning(String message, Object... args) {
        logger.log(Level.WARNING, message, args);
    }

    /**
     * 输出 Error 日志
     *
     * @param message 日志内容
     */
    public static void error(String message, Object... args) {
        logger.log(Level.SEVERE, message, args);
    }
}
