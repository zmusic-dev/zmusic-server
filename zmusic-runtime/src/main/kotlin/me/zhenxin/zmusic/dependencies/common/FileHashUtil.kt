package me.zhenxin.zmusic.dependencies.common

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.security.MessageDigest

object FileHashUtil {
    private const val BUFFER_SIZE = 8192

    @JvmStatic
    @Throws(IOException::class)
    fun sha256(file: File): String {
        return hash(file, "SHA-256")
    }

    @JvmStatic
    @Throws(IOException::class)
    fun md5(file: File): String {
        return hash(file, "MD5")
    }

    @JvmStatic
    @Throws(IOException::class)
    fun verify(file: File, expectedHash: String, algorithm: String): Boolean {
        val actualHash = when {
            algorithm.equals("SHA-256", ignoreCase = true) -> sha256(file)
            algorithm.equals("MD5", ignoreCase = true) -> md5(file)
            else -> throw IllegalArgumentException("Unsupported algorithm: $algorithm")
        }
        return actualHash.equals(expectedHash, ignoreCase = true)
    }

    private fun hash(file: File, algorithm: String): String {
        val digest = MessageDigest.getInstance(algorithm)
        FileInputStream(file).use { input ->
            val buffer = ByteArray(BUFFER_SIZE)
            while (true) {
                val read = input.read(buffer)
                if (read == -1) {
                    break
                }
                digest.update(buffer, 0, read)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }
}
