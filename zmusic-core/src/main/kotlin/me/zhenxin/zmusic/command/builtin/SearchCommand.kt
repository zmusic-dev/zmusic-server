package me.zhenxin.zmusic.command.builtin

import me.zhenxin.zmusic.command.permission.Permissions
import me.zhenxin.zmusic.config.I18n

object SearchCommand : UnavailableCommand(
    name = "search",
    permission = Permissions.PLAY,
    description = { I18n.Command.searchDescription },
    usage = "zmusic search <keyword>"
)
