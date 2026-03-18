package me.zhenxin.zmusic.dependencies.legacy

import me.lucko.jarrelocator.JarRelocator
import me.zhenxin.zmusic.dependencies.DependencyScope
import me.zhenxin.zmusic.dependencies.JarRelocation
import me.zhenxin.zmusic.dependencies.common.ClassAppender
import me.zhenxin.zmusic.dependencies.common.PrimitiveIO
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.xml.sax.SAXException
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

@Suppress("UnusedReturnValue")
class DependencyDownloader(
    private val baseDir: File?,
    relocation: List<JarRelocation>? = null,
) : AbstractXmlParser() {
    val repositories = CopyOnWriteArraySet<Repository>()
    private val relocationRules = CopyOnWriteArraySet<JarRelocation>()

    var dependencyScopes: List<DependencyScope> = listOf(DependencyScope.RUNTIME, DependencyScope.COMPILE)
    var isIgnoreOptional: Boolean = true
    var ignoreException: Boolean = false
    var isTransitive: Boolean = true

    init {
        relocation?.filterNotNull()?.forEach(relocationRules::add)
    }

    @Throws(Throwable::class)
    fun injectClasspath(dependencies: Set<Dependency>) {
        val resolvedBaseDir = requireNotNull(baseDir) { "baseDir is required for classpath injection" }
        for (dependency in dependencies) {
            val injectedLoaders = injectedDependencies[dependency]
            if (injectedLoaders != null && injectedLoaders.contains(ClassAppender.getClassLoader())) {
                continue
            }

            val file = dependency.findFile(resolvedBaseDir, "jar")
            if (file.exists()) {
                if (relocationRules.isEmpty()) {
                    val loader = ClassAppender.addPath(file.toPath(), dependency.isExternal)
                    injectedDependencies.computeIfAbsent(dependency) { hashSetOf() }.add(loader)
                } else {
                    val relocatedFile = File(
                        file.parentFile,
                        file.name.substringBeforeLast('.') + "_r2_${kotlin.math.abs(relocationRules.hashCode())}.jar",
                    )
                    if (!relocatedFile.exists() || relocatedFile.length() == 0L) {
                        try {
                            val rules = relocationRules.map(JarRelocation::toRelocation)
                            val tempSourceFile = PrimitiveIO.copyFile(file, File.createTempFile(file.name, ".jar"))
                            JarRelocator(tempSourceFile, relocatedFile, rules).run()
                        } catch (exception: IOException) {
                            throw IllegalStateException("Unable to relocate $dependency", exception)
                        }
                    }
                    val loader = ClassAppender.addPath(relocatedFile.toPath(), dependency.isExternal)
                    injectedDependencies.computeIfAbsent(dependency) { hashSetOf() }.add(loader)
                }
                continue
            }

            try {
                loadDependency(repositories, dependency)
                injectClasspath(setOf(dependency))
            } catch (exception: IOException) {
                throw IllegalStateException("Unable to load dependency: $dependency", exception)
            }
        }
    }

    @Throws(IOException::class)
    fun loadDependency(repositories: Collection<Repository>, dependency: Dependency): Set<Dependency> {
        require(repositories.isNotEmpty()) { "No repositories specified" }
        val resolvedBaseDir = requireNotNull(baseDir) { "baseDir is required for dependency download" }
        dependency.checkVersion(repositories, resolvedBaseDir)
        if (!downloadedDependencies.add(dependency)) {
            return setOf(dependency)
        }

        val pom = dependency.findFile(resolvedBaseDir, "pom")
        val pomHash = File("${pom.path}.sha1")
        val jar = dependency.findFile(resolvedBaseDir, "jar")
        val jarHash = File("${jar.path}.sha1")
        val downloaded = linkedSetOf<Dependency>()
        if (dependency.type == "jar") {
            downloaded += dependency
        }
        if (PrimitiveIO.validation(pom, pomHash) && PrimitiveIO.validation(jar, jarHash)) {
            if (pom.exists()) {
                downloaded += loadDependencyFromInputStream(pom.toURI().toURL().openStream())
            }
            return downloaded
        }

        pom.parentFile.mkdirs()
        var latestException: IOException? = null
        for (repository in repositories) {
            try {
                repository.downloadFile(dependency, pom)
                repository.downloadFile(dependency, jar)
                latestException = null
                break
            } catch (exception: Exception) {
                latestException = IOException("Unable to find download for $dependency (${repository.url})", exception)
            }
        }
        if (latestException != null) {
            throw latestException
        }
        if (pom.exists()) {
            downloaded += loadDependencyFromInputStream(pom.toURI().toURL().openStream())
        }
        return downloaded
    }

    @Throws(IOException::class)
    fun loadDependency(repositories: List<Repository>, dependencies: List<Dependency>): Set<Dependency> {
        createBaseDir()
        return buildSet {
            dependencies.forEach { addAll(loadDependency(repositories, it)) }
        }
    }

    @Throws(IOException::class)
    fun loadDependencyFromPom(pom: Document, scopes: List<DependencyScope>): Set<Dependency> {
        val dependencies = mutableListOf<Dependency>()
        val scopeSet = scopes.toSet()
        var nodes = pom.documentElement.childNodes
        val repos = repositories.toMutableList().ifEmpty { mutableListOf(Repository()) }
        try {
            var index = 0
            while (index < nodes.length) {
                val node = nodes.item(index)
                if (node.nodeName == "repositories") {
                    nodes = (node as Element).getElementsByTagName("repository")
                    for (repositoryIndex in 0 until nodes.length) {
                        repos += Repository(nodes.item(repositoryIndex) as Element)
                    }
                    break
                }
                index++
            }
        } catch (exception: java.text.ParseException) {
            throw IOException("Unable to parse repositories", exception)
        }

        if (isTransitive) {
            nodes = pom.getElementsByTagName("dependency")
            try {
                for (index in 0 until nodes.length) {
                    val node = nodes.item(index) as Element
                    if (isIgnoreOptional && find("optional", node, "false") == "true") {
                        continue
                    }
                    val dependency = Dependency(node)
                    if (scopeSet.contains(dependency.scope)) {
                        dependencies += dependency
                    }
                }
            } catch (exception: java.text.ParseException) {
                if (!ignoreException) {
                    throw IOException("Unable to parse dependencies", exception)
                }
            }
        }

        return loadDependency(repos, dependencies)
    }

    @Throws(IOException::class)
    fun loadDependencyFromInputStream(pom: InputStream): Set<Dependency> {
        return loadDependencyFromInputStream(pom, dependencyScopes)
    }

    @Throws(IOException::class)
    fun loadDependencyFromInputStream(pom: InputStream, scopes: List<DependencyScope>): Set<Dependency> {
        return try {
            val factory = DocumentBuilderFactory.newInstance().apply {
                setFeature("http://xml.org/sax/features/validation", false)
                setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false)
                setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
            }
            val xml = factory.newDocumentBuilder().parse(pom)
            loadDependencyFromPom(xml, scopes)
        } catch (exception: ParserConfigurationException) {
            throw IOException("Unable to load pom.xml parser", exception)
        } catch (exception: SAXException) {
            throw IOException("Unable to parse pom.xml", exception)
        }
    }

    fun addRepository(repository: Repository) {
        repositories.add(repository)
    }

    private fun createBaseDir() {
        baseDir?.mkdirs()
    }

    companion object {
        private val injectedDependencies = ConcurrentHashMap<Dependency, MutableSet<ClassLoader>>()
        private val downloadedDependencies = CopyOnWriteArraySet<Dependency>()
    }
}
