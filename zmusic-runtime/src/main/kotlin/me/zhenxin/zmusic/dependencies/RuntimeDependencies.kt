package me.zhenxin.zmusic.dependencies

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RuntimeDependencies(
    val value: Array<RuntimeDependency>,
)
