package me.zhenxin.zmusic.command

import me.zhenxin.zmusic.api.entity.ZCommandSender
import me.zhenxin.zmusic.api.entity.ZPlayer
import me.zhenxin.zmusic.api.service.PlatformService
import me.zhenxin.zmusic.command.permission.Permission
import me.zhenxin.zmusic.command.permission.Permissions
import net.kyori.adventure.text.Component

/**
 * 命令执行上下文
 */
data class CommandContext(
    val sender: ZCommandSender,
    val label: String,
    val args: List<String>,
    val raw: String,
    val platformService: PlatformService
) {

    fun reply(message: String) {
        platformService.messageService.sendMessage(sender, message)
    }

    fun reply(message: Component) {
        platformService.messageService.sendMessage(sender, message)
    }

    fun requirePlayer(): ZPlayer {
        return sender.asPlayer() ?: throw PlayerOnlyCommandException()
    }

    fun hasPermission(permission: Permission): Boolean {
        return sender.hasPermission(permission.node) || sender.hasPermission(Permissions.ADMIN.node)
    }

    fun checkPermission(permission: Permission) {
        if (!hasPermission(permission)) {
            throw NoPermissionCommandException()
        }
    }
}
