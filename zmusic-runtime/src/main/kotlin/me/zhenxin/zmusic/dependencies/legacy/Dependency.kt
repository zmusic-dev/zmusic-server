package me.zhenxin.zmusic.dependencies.legacy

import me.zhenxin.zmusic.dependencies.DependencyScope
import org.w3c.dom.Element
import java.io.File
import java.io.IOException
import java.net.MalformedURLException
import java.net.URI
import java.net.URL

class Dependency(
    val groupId: String,
    val artifactId: String,
    version: String,
    val scope: DependencyScope,
) : AbstractXmlParser() {
    private var resolvedVersion = if (version.contains('$') || version.contains('[') || version.contains('(')) LATEST_VERSION else version

    var type: String = "jar"
    var isExternal: Boolean = false

    constructor(node: Element) : this(
        find("groupId", node),
        find("artifactId", node),
        find("version", node, LATEST_VERSION),
        DependencyScope.valueOf(find("scope", node, "runtime").uppercase()),
    )

    @Throws(MalformedURLException::class)
    fun getURL(repo: Repository, ext: String): URL {
        val name = "$artifactId-${getVersion()}.$ext"
        return URI.create("${repo.url}/${groupId.replace('.', '/')}/$artifactId/${getVersion()}/$name").toURL()
    }

    @Throws(IOException::class)
    fun checkVersion(repositories: Collection<Repository>, baseDir: File) {
        if (getVersion() != null) {
            return
        }

        val installedLatestVersion = getInstalledLatestVersion(baseDir)
        val shouldCheckUpdate = installedLatestVersion == null || VersionChecker.isOutdated()
        if (!shouldCheckUpdate) {
            setVersion(installedLatestVersion.toString())
            return
        }

        VersionChecker.updateCheckTime()
        var latestException: IOException? = null
        for (repository in repositories) {
            try {
                repository.getLatestVersion(this)
                latestException = null
                break
            } catch (exception: IOException) {
                latestException = IOException("Unable to find latest version of $this", exception)
            }
        }
        if (latestException != null) {
            throw latestException
        }
    }

    fun getInstalledLatestVersion(baseDir: File): DependencyVersion? {
        return getInstalledVersions(baseDir).maxOrNull()
    }

    fun getInstalledVersions(dir: File): Array<DependencyVersion> {
        var currentDir = dir
        groupId.split('.').forEach { part ->
            currentDir = File(currentDir, part)
        }
        currentDir = File(currentDir, artifactId)
        val versions = currentDir.list() ?: return emptyArray()
        return versions.map(::DependencyVersion).toTypedArray()
    }

    fun findFile(dir: File, ext: String): File {
        val version = getVersion() ?: error("Version is not resolved: $this")
        var currentDir = dir
        groupId.split('.').forEach { part ->
            currentDir = File(currentDir, part)
        }
        currentDir = File(currentDir, artifactId)
        currentDir = File(currentDir, version)
        return File(currentDir, "$artifactId-$version.$ext")
    }

    fun getVersion(): String? {
        return resolvedVersion.takeUnless { it == LATEST_VERSION }
    }

    fun setVersion(version: String) {
        check(resolvedVersion == LATEST_VERSION) { "Version is already resolved" }
        require(version != LATEST_VERSION) { "Cannot set version to the latest" }
        resolvedVersion = version
    }

    override fun toString(): String {
        return "$groupId:$artifactId:$resolvedVersion"
    }

    override fun equals(other: Any?): Boolean {
        return other is Dependency &&
            groupId == other.groupId &&
            artifactId == other.artifactId &&
            getVersion() == other.getVersion()
    }

    override fun hashCode(): Int {
        return 31 * groupId.hashCode() + artifactId.hashCode()
    }

    companion object {
        private const val LATEST_VERSION = "latest"
    }
}
