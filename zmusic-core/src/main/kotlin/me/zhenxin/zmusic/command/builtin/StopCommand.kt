package me.zhenxin.zmusic.command.builtin

import me.zhenxin.zmusic.command.permission.Permissions
import me.zhenxin.zmusic.config.I18n

object StopCommand : UnavailableCommand(
    name = "stop",
    permission = Permissions.STOP,
    description = { I18n.Command.stopDescription },
    usage = "zmusic stop"
)
