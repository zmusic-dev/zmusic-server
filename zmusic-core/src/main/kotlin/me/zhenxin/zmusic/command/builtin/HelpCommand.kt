package me.zhenxin.zmusic.command.builtin

import me.zhenxin.zmusic.command.CommandContext
import me.zhenxin.zmusic.command.CommandNode
import me.zhenxin.zmusic.command.CommandNotFoundException
import me.zhenxin.zmusic.command.ZMusicCommandManager
import me.zhenxin.zmusic.command.permission.Permissions
import me.zhenxin.zmusic.config.I18n

object HelpCommand : CommandNode(
    name = "help",
    aliases = setOf("?"),
    permission = Permissions.HELP,
    description = { I18n.Command.helpDescription },
    usage = "zmusic help [command]"
) {

    override fun execute(context: CommandContext) {
        if (context.args.isEmpty()) {
            context.reply(I18n.Command.helpHeader)
            ZMusicCommandManager.visibleCommands(context.sender).forEach { command ->
                context.reply(
                    I18n.Command.helpEntry
                        .replace("{usage}", command.usage)
                        .replace("{description}", command.description())
                )
            }
            return
        }

        val target = ZMusicCommandManager.findCommand(context.args.first())
            ?: throw CommandNotFoundException(context.args.first())

        if (target !in ZMusicCommandManager.visibleCommands(context.sender)) {
            throw CommandNotFoundException(context.args.first())
        }

        context.reply(I18n.Command.helpDetailHeader.replace("{usage}", target.usage))
        replyEntry(context, target)
        ZMusicCommandManager.visibleChildren(context.sender, target).forEach { child ->
            replyEntry(context, child)
        }
    }

    private fun replyEntry(context: CommandContext, command: CommandNode) {
        context.reply(
            I18n.Command.helpEntry
                .replace("{usage}", command.usage)
                .replace("{description}", command.description())
        )
    }
}
