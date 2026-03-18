package me.zhenxin.zmusic.dependencies.exception

/**
 * 依赖下载异常
 */
class DependencyDownloadException : DependencyException {

    val coordinate: String
    val repository: String

    constructor(coordinate: String, repository: String, message: String) :
        super("Failed to download dependency '$coordinate' from '$repository': $message") {
        this.coordinate = coordinate
        this.repository = repository
    }

    constructor(coordinate: String, repository: String, message: String, cause: Throwable) :
        super("Failed to download dependency '$coordinate' from '$repository': $message", cause) {
        this.coordinate = coordinate
        this.repository = repository
    }
}
