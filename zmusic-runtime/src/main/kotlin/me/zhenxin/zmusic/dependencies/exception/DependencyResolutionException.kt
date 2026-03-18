package me.zhenxin.zmusic.dependencies.exception

/**
 * 依赖解析异常
 */
class DependencyResolutionException : DependencyException {

    val coordinate: String

    constructor(coordinate: String, message: String) :
        super("Failed to resolve dependency '$coordinate': $message") {
        this.coordinate = coordinate
    }

    constructor(coordinate: String, message: String, cause: Throwable) :
        super("Failed to resolve dependency '$coordinate': $message", cause) {
        this.coordinate = coordinate
    }
}
