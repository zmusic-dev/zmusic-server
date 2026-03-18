package me.zhenxin.zmusic.dependencies.exception

/**
 * 依赖管理基础异常类
 */
open class DependencyException : Exception {

    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)

    constructor(cause: Throwable) : super(cause)
}
