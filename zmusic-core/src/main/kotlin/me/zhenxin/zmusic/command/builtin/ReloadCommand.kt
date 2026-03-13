package me.zhenxin.zmusic.command.builtin

import me.zhenxin.zmusic.command.CommandContext
import me.zhenxin.zmusic.command.CommandNode
import me.zhenxin.zmusic.command.permission.Permissions
import me.zhenxin.zmusic.config.I18n
import me.zhenxin.zmusic.config.initConfig
import me.zhenxin.zmusic.config.initI18n
import me.zhenxin.zmusic.logger

object ReloadCommand : CommandNode(
    name = "reload",
    aliases = setOf("rl"),
    permission = Permissions.RELOAD,
    description = { I18n.Command.reloadDescription },
    usage = "zmusic reload"
) {

    override fun execute(context: CommandContext) {
        runCatching {
            initConfig()
            initI18n()
        }.onSuccess {
            context.reply(I18n.Command.reloadSuccess)
        }.onFailure { throwable ->
            logger.error("Failed to reload configuration")
            logger.error(throwable.stackTraceToString())
            context.reply(I18n.Command.reloadFailed)
        }
    }
}
