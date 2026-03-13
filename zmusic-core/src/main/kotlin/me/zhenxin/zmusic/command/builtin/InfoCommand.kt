package me.zhenxin.zmusic.command.builtin

import me.zhenxin.zmusic.command.CommandContext
import me.zhenxin.zmusic.command.CommandNode
import me.zhenxin.zmusic.command.ZMusicCommandManager
import me.zhenxin.zmusic.command.permission.Permissions
import me.zhenxin.zmusic.config.I18n

object InfoCommand : CommandNode(
    name = "info",
    permission = Permissions.INFO,
    description = { I18n.Command.infoDescription },
    usage = "zmusic info"
) {

    override fun execute(context: CommandContext) {
        context.reply(I18n.Command.infoHeader)
        context.reply(I18n.Command.infoVersion.replace("{value}", ZMusicCommandManager.versionText()))
        context.reply(I18n.Command.infoPlatform.replace("{value}", ZMusicCommandManager.platformText()))
        context.reply(I18n.Command.infoOnlinePlayers.replace("{value}", ZMusicCommandManager.onlinePlayerCount().toString()))
    }
}
