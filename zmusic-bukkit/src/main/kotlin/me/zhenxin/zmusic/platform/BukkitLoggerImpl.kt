package me.zhenxin.zmusic.platform

import me.zhenxin.zmusic.config.Config
import org.bukkit.command.CommandSender

/**
 * Bukkit 日志实现
 *
 * @author 真心
 * @since 2023/7/23 9:05
 */
class BukkitLoggerImpl(private val sender: CommandSender) : PlatformLogger {
    override fun log(msg: String) = sender.sendMessage("${Config.prefix}$msg")
}