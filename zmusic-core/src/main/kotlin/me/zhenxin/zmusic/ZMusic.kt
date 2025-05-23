package me.zhenxin.zmusic

import me.zhenxin.zmusic.config.I18n
import me.zhenxin.zmusic.config.initConfig
import me.zhenxin.zmusic.config.initI18n
import me.zhenxin.zmusic.enums.Platform
import me.zhenxin.zmusic.platform.PlatformLogger
import me.zhenxin.zmusic.utils.checkUpdate
import me.zhenxin.zmusic.utils.colored
import java.io.File
import kotlin.concurrent.thread

const val baseApi = "https://api.zhenxin.me"

/**
 * ZMusic
 *
 * @author 真心
 * @since 2023/5/23 16:57
 */
object ZMusic {

    private const val LOGO = "" +
            "  ______  __  __                 _        \n" +
            " |___  / |  \\/  |               (_)       \n" +
            "    / /  | \\  / |  _   _   ___   _    ___ \n" +
            "   / /   | |\\/| | | | | | / __| | |  / __|\n" +
            "  / /__  | |  | | | |_| | \\__ \\ | | | (__ \n" +
            " /_____| |_|  |_|  \\__,_| |___/ |_|  \\___|\n"

    /**
     * 插件启用
     */
    fun onEnable() {
        LOGO.split("\n").forEach { logger.log("&b$it".colored()) }
        logger.log("\t&6v${ZMusicConstants.PLUGIN_VERSION}\tby ZhenXin".colored())
        logger.log("")
        logger.info("Initializing ZMusic...")
        logger.info("Initializing configuration...")
        initConfig()
        logger.info("Initializing i18n...")
        initI18n()
        logger.info("ZMusic is initialized.")

        I18n.Init.loaded.forEach {
            logger.info(
                it.replace("{version}", ZMusicConstants.PLUGIN_VERSION)
                    .replace("{platform}", currentPlatform.name.lowercase())
                    .replace("{docs-url}", "zmusic.zhenxin.me")
                    .replace("{author}", "ZhenXin")
            )
        }

        thread { checkUpdate() }
    }

    /**
     * 插件禁用
     */
    fun onDisable() {
        logger.info("Disabling ZMusic...")
        logger.info("ZMusic is disabled.")
    }
}

/**
 * 当前平台
 */
lateinit var currentPlatform: Platform

/**
 * 插件数据文件夹
 */
lateinit var dataFolder: File

/**
 * 日志
 */
lateinit var logger: PlatformLogger
