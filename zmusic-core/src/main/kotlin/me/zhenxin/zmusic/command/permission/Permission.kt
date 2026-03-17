package me.zhenxin.zmusic.command.permission

/**
 * 权限节点定义
 */
data class Permission(
    val node: String,
    val description: String,
    val defaultGrantedToConsole: Boolean = true
)
