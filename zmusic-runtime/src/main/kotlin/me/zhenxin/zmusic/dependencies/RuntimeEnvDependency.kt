package me.zhenxin.zmusic.dependencies

import me.zhenxin.zmusic.dependencies.aether.AetherResolver
import me.zhenxin.zmusic.dependencies.common.BootstrapContext
import me.zhenxin.zmusic.dependencies.common.RuntimeLogger
import me.zhenxin.zmusic.dependencies.common.PrimitiveIO.t
import me.zhenxin.zmusic.dependencies.legacy.Artifact
import me.zhenxin.zmusic.dependencies.legacy.Dependency
import me.zhenxin.zmusic.dependencies.legacy.DependencyDownloader
import me.zhenxin.zmusic.dependencies.legacy.Repository
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class RuntimeEnvDependency @JvmOverloads constructor(
    val configuration: DependencyConfiguration = DependencyConfiguration.bootstrapCopy(),
) {
    var defaultLibrary: String = configuration.libraryDirectory
        set(value) {
            field = value
            configuration.libraryDirectory = value
            BootstrapContext.setDefaultLibrary(value)
        }

    @Throws(Throwable::class)
    fun loadDependency(clazz: Class<*>) {
        RuntimeDependencyProcessor.loadAnnotatedDependencies(this, clazz)
    }

    @Throws(Throwable::class)
    fun loadDependency(url: String, transitive: Boolean, relocation: List<JarRelocation>) {
        loadDependency(
            url = url,
            baseDir = File(defaultLibrary),
            relocation = relocation,
            repository = null,
            ignoreOptional = true,
            ignoreException = false,
            transitive = transitive,
            scope = DEFAULT_SCOPES,
            external = true,
        )
    }

    @Throws(Throwable::class)
    fun loadDependency(
        url: String,
        baseDir: File,
        relocation: List<JarRelocation>,
        repository: String?,
        ignoreOptional: Boolean,
        ignoreException: Boolean,
        transitive: Boolean,
        scope: List<DependencyScope>,
        external: Boolean,
    ) {
        val resolvedRepository = repository?.takeUnless { it.isEmpty() } ?: configuration.repository
        if (isAetherFound) {
            loadDependencyWithAether(
                url = url,
                relocation = relocation,
                repository = resolvedRepository,
                ignoreOptional = ignoreOptional,
                ignoreException = ignoreException,
                transitive = transitive,
                scope = scope,
                external = external,
            )
            return
        }

        loadDependencyLegacy(
            url = url,
            baseDir = baseDir,
            relocation = relocation,
            repository = resolvedRepository,
            ignoreOptional = ignoreOptional,
            ignoreException = ignoreException,
            transitive = transitive,
            scope = scope,
            external = external,
        )
    }

    @Throws(Throwable::class)
    private fun loadDependencyWithAether(
        url: String,
        relocation: List<JarRelocation>,
        repository: String,
        ignoreOptional: Boolean,
        ignoreException: Boolean,
        transitive: Boolean,
        scope: List<DependencyScope>,
        external: Boolean,
    ) {
        val startTime = RuntimeLogger.logDependencyLoadStart(url)
        try {
            val resolvedFiles = AetherResolver.of(repository).resolve(url, scope, transitive, ignoreOptional)
            RuntimeLogger.debug(
                t("开始注入 {0} 个依赖文件", "Starting to inject {0} dependency files"),
                resolvedFiles.size,
            )
            if (configuration.parallelDownload && resolvedFiles.size > 1) {
                RuntimeLogger.debug(t("使用并行模式注入依赖", "Using parallel mode to inject dependencies"))
                injectFilesParallel(resolvedFiles, relocation, external, ignoreException)
            } else {
                RuntimeLogger.debug(t("使用顺序模式注入依赖", "Using sequential mode to inject dependencies"))
                injectFilesSequential(resolvedFiles, relocation, external, ignoreException)
            }
            RuntimeLogger.logDependencyLoadSuccess(url, startTime)
        } catch (throwable: Throwable) {
            RuntimeLogger.logDependencyLoadFailure(url, startTime, throwable)
            if (!ignoreException) {
                throw throwable
            }
        }
    }

    @Throws(Throwable::class)
    private fun loadDependencyLegacy(
        url: String,
        baseDir: File,
        relocation: List<JarRelocation>,
        repository: String,
        ignoreOptional: Boolean,
        ignoreException: Boolean,
        transitive: Boolean,
        scope: List<DependencyScope>,
        external: Boolean,
    ) {
        val artifact = Artifact(url)
        val downloader = DependencyDownloader(baseDir, relocation)
        downloader.addRepository(Repository(repository))
        downloader.isIgnoreOptional = ignoreOptional
        downloader.ignoreException = ignoreException
        downloader.dependencyScopes = scope
        downloader.isTransitive = transitive

        val dependency = Dependency(artifact.groupId, artifact.artifactId, artifact.version, DependencyScope.RUNTIME)
        dependency.type = artifact.extension
        dependency.isExternal = external
        if (transitive) {
            downloader.injectClasspath(downloader.loadDependency(downloader.repositories.toList(), dependency))
        } else {
            downloader.injectClasspath(setOf(dependency))
        }
    }

    @Throws(Throwable::class)
    private fun injectFilesSequential(
        files: List<File>,
        relocation: List<JarRelocation>,
        external: Boolean,
        ignoreException: Boolean,
    ) {
        for (file in files) {
            try {
                AetherResolver.inject(file, relocation, external)
            } catch (throwable: Throwable) {
                if (!ignoreException) {
                    RuntimeLogger.warning(
                        t("✗ 注入依赖失败: {0} - {1}", "✗ Failed to inject dependency: {0} - {1}"),
                        file.name,
                        throwable.message,
                    )
                    throw throwable
                }
                RuntimeLogger.debug(
                    t("注入依赖失败(已忽略): {0} - {1}", "Failed to inject dependency (ignored): {0} - {1}"),
                    file.name,
                    throwable.message,
                )
            }
        }
    }

    @Throws(Throwable::class)
    private fun injectFilesParallel(
        files: List<File>,
        relocation: List<JarRelocation>,
        external: Boolean,
        ignoreException: Boolean,
    ) {
        val executor = Executors.newFixedThreadPool(minOf(configuration.maxParallelDownloads, files.size))
        try {
            val futures = mutableListOf<Future<File>>()
            for (file in files) {
                futures += executor.submit<File> {
                    try {
                        if (relocation.isNotEmpty()) {
                            val cacheManager = JarCacheManager.getInstance()
                            val relocatedFile = cacheManager.getRelocatedFile(file, relocation.hashCode())
                            if (cacheManager.needsRelocate(file, relocatedFile)) {
                                RuntimeLogger.debug(
                                    t("预处理重定向文件: {0}", "Pre-processing relocation for: {0}"),
                                    file.name,
                                )
                            }
                        }
                    } catch (exception: Exception) {
                        RuntimeLogger.debug(
                            t("预处理文件失败: {0} - {1}", "Failed to pre-process file: {0} - {1}"),
                            file.name,
                            exception.message,
                        )
                    }
                    file
                }
            }

            for (future in futures) {
                try {
                    future.get(30, TimeUnit.SECONDS)
                } catch (_: java.util.concurrent.TimeoutException) {
                    RuntimeLogger.debug(
                        t("预处理超时，回退到顺序模式", "Pre-processing timeout, falling back to sequential"),
                    )
                    break
                }
            }
        } finally {
            executor.shutdown()
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow()
                }
            } catch (_: InterruptedException) {
                executor.shutdownNow()
                Thread.currentThread().interrupt()
            }
        }

        injectFilesSequential(files, relocation, external, ignoreException)
    }

    companion object {
        private val DEFAULT_SCOPES = listOf(DependencyScope.RUNTIME, DependencyScope.COMPILE)

        private val isAetherFound = detectAetherAvailability()

        private fun detectAetherAvailability(): Boolean {
            var found = try {
                Class.forName("org.eclipse.aether.graph.Dependency")
                true
            } catch (_: ClassNotFoundException) {
                false
            }
            try {
                Class.forName("com.mohistmc.MohistMC")
                found = false
            } catch (_: ClassNotFoundException) {
            }
            return found
        }
    }
}
