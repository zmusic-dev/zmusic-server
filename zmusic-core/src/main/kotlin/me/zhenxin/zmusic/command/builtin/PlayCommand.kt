package me.zhenxin.zmusic.command.builtin

import me.zhenxin.zmusic.command.permission.Permissions
import me.zhenxin.zmusic.config.I18n

object PlayCommand : UnavailableCommand(
    name = "play",
    permission = Permissions.PLAY,
    description = { I18n.Command.playDescription },
    usage = "zmusic play <keyword>"
)
