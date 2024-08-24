package me.zhenxin.zmusic.platform

import me.zhenxin.zmusic.config.Config
import net.md_5.bungee.api.CommandSender


/**
 * BungeeCord 日志实现
 *
 * @author 真心
 * @since 2023/8/28 12:28
 */
@Suppress("DEPRECATION")
class BungeeLoggerImpl(private val sender: CommandSender) : PlatformLogger {
    override fun log(msg: String) = sender.sendMessage("${Config.prefix}$msg")
}