package me.zhenxin.zmusic.dependencies.common

import java.io.File
import java.io.IOException

data class CacheMetadata(
    val sha256: String,
    val fileSize: Long,
    val lastModified: Long,
    val createdTime: Long,
    val relocationHash: Int,
) {
    fun toJson(): String {
        return """{"sha256":"$sha256","fileSize":$fileSize,"lastModified":$lastModified,"createdTime":$createdTime,"relocationHash":$relocationHash}"""
    }

    @Throws(IOException::class)
    fun saveToFile(metadataFile: File) {
        metadataFile.parentFile?.mkdirs()
        metadataFile.writeText(toJson())
    }

    fun matches(file: File, relocationHash: Int): Boolean {
        if (!file.exists() || !file.isFile) {
            return false
        }
        if (this.relocationHash != relocationHash) {
            return false
        }
        if (file.length() != fileSize) {
            return false
        }
        if (file.lastModified() != lastModified) {
            return false
        }
        return try {
            sha256.equals(FileHashUtil.sha256(file), ignoreCase = true)
        } catch (_: IOException) {
            false
        }
    }

    fun isExpired(maxAgeMs: Long): Boolean {
        return System.currentTimeMillis() - createdTime > maxAgeMs
    }

    companion object {
        @JvmStatic
        @Throws(IOException::class)
        fun fromFile(file: File, relocationHash: Int): CacheMetadata {
            return CacheMetadata(
                sha256 = FileHashUtil.sha256(file),
                fileSize = file.length(),
                lastModified = file.lastModified(),
                createdTime = System.currentTimeMillis(),
                relocationHash = relocationHash,
            )
        }

        @JvmStatic
        fun fromJson(json: String): CacheMetadata? {
            return try {
                val map = parseSimpleJson(json)
                CacheMetadata(
                    sha256 = map.getValue("sha256"),
                    fileSize = map.getValue("fileSize").toLong(),
                    lastModified = map.getValue("lastModified").toLong(),
                    createdTime = map.getValue("createdTime").toLong(),
                    relocationHash = map.getValue("relocationHash").toInt(),
                )
            } catch (_: Exception) {
                null
            }
        }

        @JvmStatic
        fun loadFromFile(metadataFile: File): CacheMetadata? {
            if (!metadataFile.exists() || !metadataFile.isFile) {
                return null
            }
            return runCatching { fromJson(metadataFile.readText()) }.getOrNull()
        }

        private fun parseSimpleJson(json: String): Map<String, String> {
            val content = json.trim().removePrefix("{").removeSuffix("}")
            if (content.isBlank()) {
                return emptyMap()
            }
            return buildMap {
                content.split(',').forEach { pair ->
                    val keyValue = pair.split(':', limit = 2)
                    if (keyValue.size == 2) {
                        put(
                            keyValue[0].trim().replace("\"", ""),
                            keyValue[1].trim().replace("\"", ""),
                        )
                    }
                }
            }
        }
    }
}
