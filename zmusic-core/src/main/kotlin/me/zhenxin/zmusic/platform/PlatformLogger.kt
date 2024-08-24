package me.zhenxin.zmusic.platform

import me.zhenxin.zmusic.config.Config
import me.zhenxin.zmusic.utils.colored

/**
 * 日志
 *
 * @author 真心
 * @since 2023/7/22 20:08
 */
interface PlatformLogger {
    /**
     * 信息日志
     *
     * @param msg 日志信息
     */
    fun info(msg: String) {
        val color = "&a".colored()
        val message = msg.colored()
        log("$color$message")
    }

    /**
     * 警告日志
     *
     * @param msg 日志信息
     */
    fun warn(msg: String) {
        val color = "&e".colored()
        val message = msg.colored()
        log("$color$message")
    }

    /**
     * 错误日志
     *
     * @param msg 日志信息
     */
    fun error(msg: String) {
        val color = "&c".colored()
        val message = msg.colored()
        log("$color$message")
    }

    /**
     * 调试日志
     *
     * @param msg 日志信息
     */
    fun debug(msg: String) {
        if (Config.debug) {
            val color = "&b[Debug] ".colored()
            log("$color$msg")
        }
    }

    /**
     * 打印日志
     *
     * @param msg 日志信息
     */
    fun log(msg: String)
}
