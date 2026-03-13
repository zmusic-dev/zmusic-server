package me.zhenxin.zmusic.command

import me.zhenxin.zmusic.command.permission.Permission

/**
 * 命令节点抽象
 */
abstract class CommandNode(
    val name: String,
    val aliases: Set<String> = emptySet(),
    val permission: Permission? = null,
    val playerOnly: Boolean = false,
    val description: () -> String,
    val usage: String
) {

    fun matches(input: String): Boolean {
        return name.equals(input, ignoreCase = true) || aliases.any { it.equals(input, ignoreCase = true) }
    }

    open fun suggest(context: CommandContext): List<String> = emptyList()

    abstract fun execute(context: CommandContext)
}
