package me.zhenxin.zmusic.dependencies

import java.io.File

/**
 * Kotlin 侧运行时环境。
 * Java bootstrap 只负责预热 Kotlin，本类负责后续依赖注入所需的共享环境。
 */
class RuntimeEnv private constructor() {

    fun runtimeInit(dataFolder: String) {
        val dataFolderFile = File(dataFolder)
        if (!dataFolderFile.exists()) {
            dataFolderFile.mkdirs()
        }

        val libraryDir = File(ENV_DEPENDENCY.defaultLibrary)
        if (!libraryDir.isAbsolute) {
            ENV_DEPENDENCY.defaultLibrary = File(dataFolderFile, ENV_DEPENDENCY.defaultLibrary).path
        }

        JarCacheManager.getInstance().initialize()
    }

    @Throws(Throwable::class)
    fun inject(clazz: Class<*>) {
        ENV_DEPENDENCY.loadDependency(clazz)
    }

    companion object {
        @JvmField
        val ENV = RuntimeEnv()

        @JvmField
        val ENV_DEPENDENCY = RuntimeEnvDependency(DependencyConfiguration.bootstrapCopy())
    }
}
