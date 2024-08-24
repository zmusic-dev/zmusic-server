package me.zhenxin.zmusic.platform

import com.velocitypowered.api.command.CommandSource
import me.zhenxin.zmusic.config.Config
import me.zhenxin.zmusic.utils.uncolored
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

/**
 * Velocity 日志实现
 *
 * @author 真心
 * @since 2023/7/24 11:01
 */
class VelocityLoggerImpl(private val sender: CommandSource) : PlatformLogger {
    override fun log(msg: String) = sender.sendMessage(
        LegacyComponentSerializer.legacyAmpersand().deserialize("${Config.prefix}$msg".uncolored())
    )
}