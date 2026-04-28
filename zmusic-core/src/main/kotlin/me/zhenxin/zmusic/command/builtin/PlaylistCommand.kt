package me.zhenxin.zmusic.command.builtin

import me.zhenxin.zmusic.command.CommandNode
import me.zhenxin.zmusic.command.permission.Permission
import me.zhenxin.zmusic.command.permission.Permissions
import me.zhenxin.zmusic.config.I18n

object PlaylistCommand : UnavailableCommand(
    name = "playlist",
    aliases = setOf("pl"),
    permission = Permissions.PLAYLIST,
    description = { I18n.Command.playlistDescription },
    usage = "zmusic playlist <list|create|delete|add|remove|play|global>",
    children = listOf(
        PlaylistListCommand,
        PlaylistCreateCommand,
        PlaylistDeleteCommand,
        PlaylistAddCommand,
        PlaylistRemoveCommand,
        PlaylistPlayCommand,
        PlaylistGlobalCommand
    )
)

private object PlaylistListCommand : PlaylistSubCommand(
    name = "list",
    description = { I18n.Command.playlistListDescription },
    usage = "zmusic playlist list"
)

private object PlaylistCreateCommand : PlaylistSubCommand(
    name = "create",
    description = { I18n.Command.playlistCreateDescription },
    usage = "zmusic playlist create <name>"
)

private object PlaylistDeleteCommand : PlaylistSubCommand(
    name = "delete",
    description = { I18n.Command.playlistDeleteDescription },
    usage = "zmusic playlist delete <name>"
)

private object PlaylistAddCommand : PlaylistSubCommand(
    name = "add",
    description = { I18n.Command.playlistAddDescription },
    usage = "zmusic playlist add <playlist> <song>"
)

private object PlaylistRemoveCommand : PlaylistSubCommand(
    name = "remove",
    description = { I18n.Command.playlistRemoveDescription },
    usage = "zmusic playlist remove <playlist> <song>"
)

private object PlaylistPlayCommand : PlaylistSubCommand(
    name = "play",
    description = { I18n.Command.playlistPlayDescription },
    usage = "zmusic playlist play <playlist>"
)

private object PlaylistGlobalCommand : PlaylistSubCommand(
    name = "global",
    permission = Permissions.ADMIN,
    description = { I18n.Command.playlistGlobalDescription },
    usage = "zmusic playlist global <list|create|delete|add|remove|play>",
    children = listOf(
        PlaylistGlobalListCommand,
        PlaylistGlobalCreateCommand,
        PlaylistGlobalDeleteCommand,
        PlaylistGlobalAddCommand,
        PlaylistGlobalRemoveCommand,
        PlaylistGlobalPlayCommand
    )
)

private object PlaylistGlobalListCommand : PlaylistSubCommand(
    name = "list",
    permission = Permissions.ADMIN,
    description = { I18n.Command.playlistGlobalListDescription },
    usage = "zmusic playlist global list"
)

private object PlaylistGlobalCreateCommand : PlaylistSubCommand(
    name = "create",
    permission = Permissions.ADMIN,
    description = { I18n.Command.playlistGlobalCreateDescription },
    usage = "zmusic playlist global create <name>"
)

private object PlaylistGlobalDeleteCommand : PlaylistSubCommand(
    name = "delete",
    permission = Permissions.ADMIN,
    description = { I18n.Command.playlistGlobalDeleteDescription },
    usage = "zmusic playlist global delete <name>"
)

private object PlaylistGlobalAddCommand : PlaylistSubCommand(
    name = "add",
    permission = Permissions.ADMIN,
    description = { I18n.Command.playlistGlobalAddDescription },
    usage = "zmusic playlist global add <playlist> <song>"
)

private object PlaylistGlobalRemoveCommand : PlaylistSubCommand(
    name = "remove",
    permission = Permissions.ADMIN,
    description = { I18n.Command.playlistGlobalRemoveDescription },
    usage = "zmusic playlist global remove <playlist> <song>"
)

private object PlaylistGlobalPlayCommand : PlaylistSubCommand(
    name = "play",
    permission = Permissions.ADMIN,
    description = { I18n.Command.playlistGlobalPlayDescription },
    usage = "zmusic playlist global play <playlist>"
)

private open class PlaylistSubCommand(
    name: String,
    permission: Permission = Permissions.PLAYLIST,
    extraPermissions: Set<Permission> = emptySet(),
    strictPermissions: Set<Permission> = emptySet(),
    description: () -> String,
    usage: String,
    children: List<CommandNode> = emptyList()
) : UnavailableCommand(
    name = name,
    permission = permission,
    extraPermissions = extraPermissions,
    strictPermissions = strictPermissions,
    description = description,
    usage = usage,
    children = children
)
