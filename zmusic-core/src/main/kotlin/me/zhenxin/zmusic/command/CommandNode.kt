package me.zhenxin.zmusic.command

import me.zhenxin.zmusic.command.permission.Permission

/**
 * 命令节点抽象
 */
abstract class CommandNode(
    val name: String,
    val aliases: Set<String> = emptySet(),
    val permission: Permission? = null,
    val extraPermissions: Set<Permission> = emptySet(),
    val strictPermissions: Set<Permission> = emptySet(),
    val playerOnly: Boolean = false,
    val description: () -> String,
    val usage: String,
    val children: List<CommandNode> = emptyList()
) {

    fun matches(input: String): Boolean {
        return name.equals(input, ignoreCase = true) || aliases.any { it.equals(input, ignoreCase = true) }
    }

    fun findChild(input: String): CommandNode? {
        return children.firstOrNull { it.matches(input) }
    }

    open fun suggest(context: CommandContext): List<String> = emptyList()

    abstract fun execute(context: CommandContext)
}
