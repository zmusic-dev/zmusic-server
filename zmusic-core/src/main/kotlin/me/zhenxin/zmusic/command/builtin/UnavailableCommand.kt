package me.zhenxin.zmusic.command.builtin

import me.zhenxin.zmusic.command.CommandContext
import me.zhenxin.zmusic.command.CommandNode
import me.zhenxin.zmusic.command.UsageCommandException
import me.zhenxin.zmusic.command.permission.Permission
import me.zhenxin.zmusic.config.I18n

open class UnavailableCommand(
    name: String,
    aliases: Set<String> = emptySet(),
    permission: Permission,
    extraPermissions: Set<Permission> = emptySet(),
    strictPermissions: Set<Permission> = emptySet(),
    description: () -> String,
    usage: String,
    children: List<CommandNode> = emptyList()
) : CommandNode(
    name = name,
    aliases = aliases,
    permission = permission,
    extraPermissions = extraPermissions,
    strictPermissions = strictPermissions,
    description = description,
    usage = usage,
    children = children
) {

    override fun execute(context: CommandContext) {
        if (children.isNotEmpty() && context.args.isNotEmpty()) {
            throw UsageCommandException(usage)
        }
        context.reply(I18n.Command.featureUnavailable)
    }
}
