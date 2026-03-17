package me.zhenxin.zmusic.command

/**
 * 命令异常基类
 */
sealed class CommandException(message: String) : RuntimeException(message)

class NoPermissionCommandException : CommandException("no_permission")

class PlayerOnlyCommandException : CommandException("player_only")

class CommandNotFoundException(val input: String) : CommandException("unknown_subcommand")

class UsageCommandException(val usage: String) : CommandException("invalid_usage")
