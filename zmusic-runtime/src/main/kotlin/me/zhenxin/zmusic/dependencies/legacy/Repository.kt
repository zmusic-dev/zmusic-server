package me.zhenxin.zmusic.dependencies.legacy

import me.zhenxin.zmusic.dependencies.common.PrimitiveIO
import org.w3c.dom.Element
import java.io.File
import java.io.IOException
import java.net.URI
import java.text.ParseException
import javax.xml.parsers.DocumentBuilderFactory

class Repository(
    val url: String = DEFAULT_URL,
) : AbstractXmlParser() {
    constructor(node: Element) : this(normalizeUrl(find("url", node, null)))

    @Throws(IOException::class)
    fun downloadFile(dep: Dependency, out: File) {
        val extension = out.name.substringAfterLast('.')
        PrimitiveIO.downloadFile(dep.getURL(this, extension), out)
        PrimitiveIO.downloadFile(dep.getURL(this, "$extension.sha1"), File("${out.path}.sha1"))
    }

    @Throws(IOException::class)
    fun getLatestVersion(dep: Dependency) {
        val metadataUri = URI.create("$url/${dep.groupId.replace('.', '/')}/${dep.artifactId}/maven-metadata.xml")
        try {
            val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            metadataUri.toURL().openStream().use { input ->
                val document = builder.parse(input)
                dep.setVersion(find("release", document.documentElement, find("version", document.documentElement, null)))
            }
        } catch (exception: IOException) {
            throw exception
        } catch (exception: Exception) {
            throw IOException(exception)
        }
    }

    override fun equals(other: Any?): Boolean {
        return other is Repository && url == other.url
    }

    override fun hashCode(): Int {
        return url.hashCode()
    }

    override fun toString(): String {
        return "Repository{url='$url'}"
    }

    companion object {
        private const val DEFAULT_URL = "https://maven.aliyun.com/repository/central"

        private fun normalizeUrl(url: String?): String {
            val resolvedUrl = url ?: DEFAULT_URL
            return if (resolvedUrl.endsWith("/")) resolvedUrl.dropLast(1) else resolvedUrl
        }
    }
}
