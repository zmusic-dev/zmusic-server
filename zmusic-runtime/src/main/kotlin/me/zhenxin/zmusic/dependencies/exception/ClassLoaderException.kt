package me.zhenxin.zmusic.dependencies.exception

import java.io.File

/**
 * ClassLoader 操作异常
 */
class ClassLoaderException : DependencyException {

    val jarFile: File
    val classLoaderType: String

    constructor(jarFile: File, classLoaderType: String, message: String) :
        super("Failed to inject '${jarFile.name}' into $classLoaderType: $message") {
        this.jarFile = jarFile
        this.classLoaderType = classLoaderType
    }

    constructor(jarFile: File, classLoaderType: String, message: String, cause: Throwable) :
        super("Failed to inject '${jarFile.name}' into $classLoaderType: $message", cause) {
        this.jarFile = jarFile
        this.classLoaderType = classLoaderType
    }
}
