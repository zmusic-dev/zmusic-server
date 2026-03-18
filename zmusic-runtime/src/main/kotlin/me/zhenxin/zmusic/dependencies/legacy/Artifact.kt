package me.zhenxin.zmusic.dependencies.legacy

import java.util.regex.Pattern

class Artifact(coords: String) {
    val groupId: String
    val artifactId: String
    val version: String
    val classifier: String
    val extension: String

    init {
        val matcher = COORDINATE_PATTERN.matcher(coords)
        require(matcher.matches()) {
            "Bad artifact coordinates $coords, expected format is <groupId>:<artifactId>[:<extension>[:<classifier>]]:<version>"
        }
        groupId = matcher.group(1)
        artifactId = matcher.group(2)
        extension = defaultIfBlank(matcher.group(4), "jar")
        classifier = defaultIfBlank(matcher.group(6), "")
        version = matcher.group(7)
    }

    override fun toString(): String {
        return buildString(128) {
            append(groupId)
            append(':').append(artifactId)
            append(':').append(extension)
            if (classifier.isNotEmpty()) {
                append(':').append(classifier)
            }
            append(':').append(version)
        }
    }

    companion object {
        private val COORDINATE_PATTERN = Pattern.compile("([^: ]+):([^: ]+)(:([^: ]*)(:([^: ]+))?)?:([^: ]+)")

        private fun defaultIfBlank(value: String?, defaultValue: String): String {
            return if (value.isNullOrEmpty()) defaultValue else value
        }
    }
}
