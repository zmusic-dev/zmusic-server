package me.zhenxin.zmusic.command

import me.zhenxin.zmusic.ZMusicConstants
import me.zhenxin.zmusic.api.entity.ZCommandSender
import me.zhenxin.zmusic.command.builtin.HelpCommand
import me.zhenxin.zmusic.command.builtin.InfoCommand
import me.zhenxin.zmusic.command.builtin.ReloadCommand
import me.zhenxin.zmusic.command.permission.Permission
import me.zhenxin.zmusic.command.permission.Permissions
import me.zhenxin.zmusic.config.I18n
import me.zhenxin.zmusic.currentPlatform
import me.zhenxin.zmusic.logger
import me.zhenxin.zmusic.platformService

/**
 * ZMusic 命令管理器
 */
object ZMusicCommandManager {

    const val ROOT_COMMAND = "zmusic"

    private val commands: List<CommandNode> = listOf(
        HelpCommand,
        InfoCommand,
        ReloadCommand
    )

    fun execute(sender: ZCommandSender, label: String, args: Array<out String>): Boolean {
        val context = createContext(sender, label, args.toList())

        return try {
            context.checkPermission(Permissions.USE)

            if (args.isEmpty()) {
                dispatch(HelpCommand, context.copy(args = emptyList(), raw = ""))
                return true
            }

            val command = findCommand(args.first()) ?: throw CommandNotFoundException(args.first())
            dispatch(command, createContext(sender, label, args.drop(1)))
            true
        } catch (ex: CommandException) {
            handleCommandException(context, ex)
            true
        } catch (ex: Exception) {
            logger.error("Failed to execute command /$label ${args.joinToString(" ")}")
            logger.error(ex.stackTraceToString())
            context.reply(I18n.Command.internalError)
            true
        }
    }

    fun suggest(sender: ZCommandSender, label: String, args: Array<out String>): List<String> {
        val context = createContext(sender, label, args.toList())

        if (!context.hasPermission(Permissions.USE)) {
            return emptyList()
        }

        return when {
            args.isEmpty() -> visibleCommands(sender).map { it.name }
            args.size == 1 -> {
                val input = args.first()
                visibleCommands(sender)
                    .map { it.name }
                    .filter { it.startsWith(input, ignoreCase = true) }
            }

            else -> {
                val command = findCommand(args.first()) ?: return emptyList()
                if (!canExecute(sender, command.permission) || (command.playerOnly && !sender.isPlayer)) {
                    return emptyList()
                }
                command.suggest(createContext(sender, label, args.drop(1)))
                    .filter { it.startsWith(args.last(), ignoreCase = true) }
            }
        }
    }

    fun visibleCommands(sender: ZCommandSender): List<CommandNode> {
        return commands.filter { canExecute(sender, it.permission) }
    }

    fun findCommand(name: String): CommandNode? {
        return commands.firstOrNull { it.matches(name) }
    }

    fun rootPermission(): Permission = Permissions.USE

    fun versionText(): String = ZMusicConstants.PLUGIN_VERSION

    fun platformText(): String = currentPlatform.name.lowercase()

    fun onlinePlayerCount(): Int = platformService.playerService.getOnlinePlayerCount()

    private fun createContext(sender: ZCommandSender, label: String, args: List<String>): CommandContext {
        return CommandContext(
            sender = sender,
            label = label,
            args = args,
            raw = args.joinToString(" "),
            platformService = platformService
        )
    }

    private fun dispatch(command: CommandNode, context: CommandContext) {
        command.permission?.let(context::checkPermission)
        if (command.playerOnly) {
            context.requirePlayer()
        }
        command.execute(context)
    }

    private fun handleCommandException(context: CommandContext, ex: CommandException) {
        when (ex) {
            is NoPermissionCommandException -> context.reply(I18n.Command.noPermission)
            is PlayerOnlyCommandException -> context.reply(I18n.Command.playerOnly)
            is CommandNotFoundException -> {
                context.reply(I18n.Command.unknownSubcommand.replace("{input}", ex.input))
                dispatch(HelpCommand, context.copy(args = emptyList(), raw = ""))
            }

            is UsageCommandException -> {
                context.reply(I18n.Command.invalidUsage.replace("{usage}", ex.usage))
            }
        }
    }

    private fun canExecute(sender: ZCommandSender, permission: Permission?): Boolean {
        if (permission == null) {
            return true
        }
        return sender.hasPermission(permission.node) || sender.hasPermission(Permissions.ADMIN.node)
    }
}
