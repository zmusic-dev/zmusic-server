package me.zhenxin.zmusic.dependencies

@java.lang.annotation.Repeatable(RuntimeDependencies::class)
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RuntimeDependency(
    /**
     * 依赖地址，格式为：
     * <groupId>:<artifactId>[:<extension>[:<classifier>]]:<version>
     */
    val value: String,
    /**
     * 测试类
     *
     * `test = "!org.bukkit.Bukkit"` 前面带个感叹号避免在编译时重定向
     */
    val test: String = "",
    /**
     * 仓库地址，留空默认使用阿里云中央仓库。
     */
    val repository: String = "",
    /**
     * 是否进行依赖传递
     */
    val transitive: Boolean = true,
    /**
     * 忽略可选依赖
     */
    val ignoreOptional: Boolean = true,
    /**
     * 忽略加载异常
     */
    val ignoreException: Boolean = false,
    /**
     * 依赖范围
     */
    val scopes: Array<DependencyScope> = [DependencyScope.RUNTIME, DependencyScope.COMPILE],
    /**
     * 依赖重定向
     *
     * `relocate = ["!taboolib.", "!taboolib610."]`
     */
    val relocate: Array<String> = [],
    /**
     * 是否外部库（不会被扫到）
     */
    val external: Boolean = true,
)
