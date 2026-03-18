package me.zhenxin.zmusic.dependencies.aether

import me.lucko.jarrelocator.JarRelocator
import me.lucko.jarrelocator.Relocation
import me.zhenxin.zmusic.dependencies.DependencyConfiguration
import me.zhenxin.zmusic.dependencies.DependencyScope
import me.zhenxin.zmusic.dependencies.JarCacheManager
import me.zhenxin.zmusic.dependencies.JarRelocation
import me.zhenxin.zmusic.dependencies.common.ClassAppender
import me.zhenxin.zmusic.dependencies.common.PrimitiveIO
import me.zhenxin.zmusic.dependencies.common.RuntimeLogger
import org.apache.maven.repository.internal.MavenRepositorySystemUtils
import org.eclipse.aether.DefaultRepositorySystemSession
import org.eclipse.aether.RepositorySystem
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.collection.CollectRequest
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory
import org.eclipse.aether.graph.Dependency
import org.eclipse.aether.graph.DependencyFilter
import org.eclipse.aether.graph.DependencyNode
import org.eclipse.aether.impl.DefaultServiceLocator
import org.eclipse.aether.repository.LocalRepository
import org.eclipse.aether.repository.RemoteRepository
import org.eclipse.aether.resolution.DependencyRequest
import org.eclipse.aether.resolution.DependencyResolutionException
import org.eclipse.aether.resolution.DependencyResult
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory
import org.eclipse.aether.spi.connector.transport.TransporterFactory
import org.eclipse.aether.transport.http.HttpTransporterFactory
import java.io.File
import java.io.IOException
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap

@Suppress("deprecation")
class AetherResolver(repo: String) {
    private val repository: RepositorySystem
    private val session: DefaultRepositorySystemSession
    private val repositories: List<RemoteRepository>

    init {
        val locator = MavenRepositorySystemUtils.newServiceLocator().apply {
            addService(RepositoryConnectorFactory::class.java, BasicRepositoryConnectorFactory::class.java)
            addService(TransporterFactory::class.java, HttpTransporterFactory::class.java)
        }
        repository = locator.getService(RepositorySystem::class.java)
        session = MavenRepositorySystemUtils.newSession().apply {
            checksumPolicy = "fail"
            localRepositoryManager = repository.newLocalRepositoryManager(this, LocalRepository("libraries"))
            setReadOnly()
        }
        repositories = repository.newResolutionRepositories(
            session,
            listOf(RemoteRepository.Builder("central", "default", repo).build()),
        )
    }

    @Throws(DependencyResolutionException::class)
    fun resolve(
        library: String,
        scope: List<DependencyScope>,
        isTransitive: Boolean,
        ignoreOptional: Boolean,
    ): List<File> {
        RuntimeLogger.debug(
            PrimitiveIO.t("解析依赖: {0} (传递性: {1})", "Resolving dependency: {0} (transitive: {1})"),
            library,
            isTransitive,
        )
        return resolveWithRetry(library, scope, isTransitive, ignoreOptional, DependencyConfiguration.getGlobal().retryCount)
    }

    @Throws(DependencyResolutionException::class)
    private fun resolveWithRetry(
        library: String,
        scope: List<DependencyScope>,
        isTransitive: Boolean,
        ignoreOptional: Boolean,
        maxRetries: Int,
    ): List<File> {
        val dependency = Dependency(DefaultArtifact(library), null)
        val dependencyRequest = getDependencyRequest(dependency, scope, isTransitive, ignoreOptional)

        var lastException: DependencyResolutionException? = null
        for (attempt in 1..maxRetries) {
            try {
                val result = repository.resolveDependencies(session, dependencyRequest)
                val files = result.artifactResults.map { it.artifact.file }
                RuntimeLogger.debug(
                    PrimitiveIO.t("解析完成: {0}, 获得 {1} 个文件", "Resolution completed: {0}, got {1} files"),
                    library,
                    files.size,
                )
                return files
            } catch (exception: DependencyResolutionException) {
                lastException = exception
                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(1000L * attempt)
                    } catch (interrupted: InterruptedException) {
                        Thread.currentThread().interrupt()
                        throw DependencyResolutionException(DependencyResult(dependencyRequest), "Interrupted during retry", interrupted)
                    }
                }
            }
        }
        throw checkNotNull(lastException)
    }

    private fun getDependencyRequest(
        dependency: Dependency,
        scope: List<DependencyScope>,
        isTransitive: Boolean,
        ignoreOptional: Boolean,
    ): DependencyRequest {
        return DependencyRequest(
            CollectRequest(dependency, null, repositories),
            object : DependencyFilter {
                private var self = true

                override fun accept(node: DependencyNode, parents: List<DependencyNode>): Boolean {
                    if (ignoreOptional && node.dependency.isOptional) {
                        return false
                    }
                    if (isTransitive) {
                        return true
                    }
                    if (self) {
                        self = false
                        return true
                    }
                    return false
                }
            },
        )
    }

    companion object {
        private val resolverMap = ConcurrentHashMap<String, AetherResolver>()
        private val injectedDependencies = ConcurrentHashMap.newKeySet<String>()

        @JvmStatic
        fun of(repository: String): AetherResolver {
            return resolverMap.computeIfAbsent(repository, ::AetherResolver)
        }

        @JvmStatic
        @Throws(Exception::class)
        fun inject(file: File, relocation: List<JarRelocation>?, isExternal: Boolean) {
            val filePath = file.path
            if (!injectedDependencies.add(filePath)) {
                RuntimeLogger.debug(
                    PrimitiveIO.t("跳过已加载的依赖: {0}", "Skipping already loaded dependency: {0}"),
                    file.name,
                )
                return
            }
            RuntimeLogger.debug(
                PrimitiveIO.t("正在注入依赖文件: {0} ({1} bytes)", "Injecting dependency file: {0} ({1} bytes)"),
                file.name,
                file.length(),
            )
            if (relocation.isNullOrEmpty()) {
                ClassAppender.addPath(file.toPath(), isExternal)
                RuntimeLogger.debug(PrimitiveIO.t("直接注入: {0}", "Direct injection: {0}"), file.name)
                return
            }

            val relocationHash = relocation.hashCode()
            val cacheManager = JarCacheManager.getInstance()
            val relocatedFile = cacheManager.getRelocatedFile(file, relocationHash)
            if (cacheManager.needsRelocate(file, relocatedFile, relocationHash)) {
                try {
                    val rules = relocation.map(JarRelocation::toRelocation)
                    val tempSourceFile = PrimitiveIO.copyFile(file, File.createTempFile(file.name, ".jar"))
                    JarRelocator(tempSourceFile, relocatedFile, rules).run()
                    cacheManager.saveMetadata(file, relocatedFile, relocationHash)
                } catch (exception: IOException) {
                    throw IllegalStateException("Unable to relocate $file", exception)
                }
            }
            ClassAppender.addPath(relocatedFile.toPath(), isExternal)
            RuntimeLogger.debug(
                PrimitiveIO.t("注入重定向文件: {0} -> {1}", "Injecting relocated file: {0} -> {1}"),
                file.name,
                relocatedFile.name,
            )
        }
    }
}
