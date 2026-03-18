package me.zhenxin.zmusic.dependencies.common

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.URL
import java.nio.channels.FileChannel
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Locale
import java.util.UUID

object PrimitiveIO {
    private const val BUFFER_SIZE = 8192
    private val hexArray = "0123456789abcdef".toCharArray()
    private val digestThreadLocal = ThreadLocal.withInitial<MessageDigest> {
        try {
            MessageDigest.getInstance("SHA-1")
        } catch (exception: NoSuchAlgorithmException) {
            throw RuntimeException(exception)
        }
    }
    private val chineseEnvironment = runCatching {
        Locale.getDefault().toLanguageTag().startsWith("zh")
    }.getOrDefault(true)

    @JvmStatic
    fun validation(file: File, hashFile: File): Boolean {
        return file.exists() && hashFile.exists() && readFile(hashFile).startsWith(getHash(file))
    }

    @JvmStatic
    fun getHash(file: File): String {
        val digest = digestThreadLocal.get().apply { reset() }
        return try {
            Files.newInputStream(file.toPath()).use { inputStream ->
                val buffer = ByteArray(BUFFER_SIZE)
                while (true) {
                    val read = inputStream.read(buffer)
                    if (read == -1) {
                        break
                    }
                    digest.update(buffer, 0, read)
                }
            }
            bytesToHex(digest.digest())
        } catch (exception: IOException) {
            RuntimeLogger.warning(
                t("计算文件哈希失败: {0}", "Failed to calculate file hash: {0}"),
                exception.message,
            )
            "null (${UUID.randomUUID()})"
        }
    }

    @JvmStatic
    fun bytesToHex(bytes: ByteArray): String {
        val hexChars = CharArray(bytes.size * 2)
        for (index in bytes.indices) {
            val value = bytes[index].toInt() and 0xFF
            hexChars[index * 2] = hexArray[value ushr 4]
            hexChars[index * 2 + 1] = hexArray[value and 0x0F]
        }
        return String(hexChars)
    }

    @JvmStatic
    fun readFile(file: File): String {
        return try {
            FileInputStream(file).use { readFully(it, StandardCharsets.UTF_8) }
        } catch (exception: IOException) {
            RuntimeLogger.warning(
                t("读取文件失败: {0} - {1}", "Failed to read file: {0} - {1}"),
                file.name,
                exception.message,
            )
            "null (${UUID.randomUUID()})"
        }
    }

    @JvmStatic
    @Throws(IOException::class)
    fun readFully(inputStream: InputStream, charset: Charset): String {
        return String(readFully(inputStream), charset)
    }

    @JvmStatic
    @Throws(IOException::class)
    fun readFully(inputStream: InputStream): ByteArray {
        val stream = ByteArrayOutputStream()
        val buffer = ByteArray(BUFFER_SIZE)
        while (true) {
            val read = inputStream.read(buffer)
            if (read <= 0) {
                break
            }
            stream.write(buffer, 0, read)
        }
        return stream.toByteArray()
    }

    @JvmStatic
    fun copyFile(from: File, to: File): File {
        return try {
            FileInputStream(from).use { fileIn ->
                FileOutputStream(to).use { fileOut ->
                    fileIn.channel.use { channelIn ->
                        fileOut.channel.use { channelOut ->
                            channelIn.transferTo(0, channelIn.size(), channelOut)
                        }
                    }
                }
            }
            to
        } catch (throwable: IOException) {
            RuntimeLogger.warning(
                t("复制文件失败: {0} -> {1} - {2}", "Failed to copy file: {0} -> {1} - {2}"),
                from.name,
                to.name,
                throwable.message,
            )
            to
        }
    }

    @JvmStatic
    @Throws(IOException::class)
    fun downloadFile(url: URL, out: File) {
        out.parentFile?.mkdirs()
        url.openStream().use { input ->
            Files.newOutputStream(out.toPath()).use { output ->
                copy(input, output)
            }
        }
    }

    @JvmStatic
    fun t(zh: String, en: String): String {
        return if (chineseEnvironment) zh else en
    }

    @Throws(IOException::class)
    private fun copy(input: InputStream, output: OutputStream) {
        val buffer = ByteArray(BUFFER_SIZE)
        while (true) {
            val read = input.read(buffer)
            if (read <= 0) {
                return
            }
            output.write(buffer, 0, read)
        }
    }
}
