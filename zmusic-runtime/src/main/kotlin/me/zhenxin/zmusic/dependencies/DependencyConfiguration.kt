package me.zhenxin.zmusic.dependencies

import me.zhenxin.zmusic.dependencies.common.BootstrapContext
import java.io.File
import java.io.FileInputStream
import java.util.Properties

class DependencyConfiguration {
    var repository: String = loadProperty("zmusic.dependency.repository", DEFAULT_REPOSITORY)
        set(value) {
            require(value.isNotBlank()) { "Repository URL cannot be null or empty" }
            field = value.trim()
        }

    var libraryDirectory: String = loadProperty("zmusic.dependency.library", DEFAULT_LIBRARY_DIR)
        set(value) {
            require(value.isNotBlank()) { "Library directory cannot be null or empty" }
            field = value.trim()
        }

    var retryCount: Int = loadIntProperty("zmusic.dependency.retry", DEFAULT_RETRY_COUNT)
        set(value) {
            require(value >= 0) { "Retry count cannot be negative" }
            field = value
        }

    var timeoutMillis: Int = loadIntProperty("zmusic.dependency.timeout", DEFAULT_TIMEOUT)
        set(value) {
            require(value > 0) { "Timeout must be positive" }
            field = value
        }

    var verboseLogging: Boolean = loadBoolProperty("zmusic.dependency.verbose", DEFAULT_VERBOSE)
    var parallelDownload: Boolean = loadBoolProperty("zmusic.dependency.parallel", DEFAULT_PARALLEL)

    var maxParallelDownloads: Int = loadIntProperty("zmusic.dependency.parallel.max", DEFAULT_PARALLEL_MAX)
        set(value) {
            require(value > 0) { "Max parallel downloads must be positive" }
            field = value
        }

    var cleanupIntervalHours: Int = loadIntProperty("zmusic.dependency.cleanup.interval", DEFAULT_CLEANUP_INTERVAL)
        set(value) {
            require(value > 0) { "Cleanup interval must be positive" }
            field = value
        }

    var maxFileAgeHours: Int = loadIntProperty("zmusic.dependency.file.maxage", DEFAULT_MAX_FILE_AGE)
        set(value) {
            require(value > 0) { "Max file age must be positive" }
            field = value
        }

    fun validate() {
        check(repository.isNotBlank()) { "Repository URL is required" }
        check(libraryDirectory.isNotBlank()) { "Library directory is required" }
        check(retryCount >= 0) { "Retry count cannot be negative" }
        check(timeoutMillis > 0) { "Timeout must be positive" }
        check(maxParallelDownloads > 0) { "Max parallel downloads must be positive" }
    }

    fun copy(): DependencyConfiguration {
        return DependencyConfiguration().also { copy ->
            copy.repository = repository
            copy.libraryDirectory = libraryDirectory
            copy.retryCount = retryCount
            copy.timeoutMillis = timeoutMillis
            copy.verboseLogging = verboseLogging
            copy.parallelDownload = parallelDownload
            copy.maxParallelDownloads = maxParallelDownloads
            copy.cleanupIntervalHours = cleanupIntervalHours
            copy.maxFileAgeHours = maxFileAgeHours
        }
    }

    override fun toString(): String {
        return "DependencyConfiguration(" +
            "repository='$repository', " +
            "libraryDirectory='$libraryDirectory', " +
            "retryCount=$retryCount, " +
            "timeoutMillis=$timeoutMillis, " +
            "verboseLogging=$verboseLogging, " +
            "parallelDownload=$parallelDownload, " +
            "maxParallelDownloads=$maxParallelDownloads, " +
            "cleanupIntervalHours=$cleanupIntervalHours, " +
            "maxFileAgeHours=$maxFileAgeHours" +
            ")"
    }

    companion object {
        private const val DEFAULT_REPOSITORY = "https://maven.aliyun.com/repository/central"
        private const val DEFAULT_LIBRARY_DIR = "libraries"
        private const val DEFAULT_RETRY_COUNT = 3
        private const val DEFAULT_TIMEOUT = 30000
        private const val DEFAULT_VERBOSE = false
        private const val DEFAULT_PARALLEL = false
        private const val DEFAULT_PARALLEL_MAX = 3
        private const val DEFAULT_CLEANUP_INTERVAL = 24
        private const val DEFAULT_MAX_FILE_AGE = 168

        private val GLOBAL = DependencyConfiguration()

        @JvmStatic
        fun getGlobal(): DependencyConfiguration {
            return GLOBAL
        }

        @JvmStatic
        fun bootstrapCopy(): DependencyConfiguration {
            return getGlobal().copy().apply {
                repository = BootstrapContext.getRepository()
                libraryDirectory = BootstrapContext.getDefaultLibrary()
            }
        }

        @JvmStatic
        fun fromToml(configFile: File): DependencyConfiguration {
            val config = DependencyConfiguration()
            if (!configFile.exists() || !configFile.isFile) {
                return config
            }

            try {
                FileInputStream(configFile).use { input ->
                    val props = Properties().apply { load(input) }
                    config.repository = props.getProperty("dependency.repository", config.repository)
                    config.libraryDirectory = props.getProperty("dependency.library", config.libraryDirectory)
                    config.retryCount = parseIntProperty(props, "dependency.retry", config.retryCount)
                    config.timeoutMillis = parseIntProperty(props, "dependency.timeout", config.timeoutMillis)
                    config.verboseLogging = parseBoolProperty(props, "dependency.verbose", config.verboseLogging)
                    config.parallelDownload = parseBoolProperty(props, "parallel.enabled", config.parallelDownload)
                    config.maxParallelDownloads = parseIntProperty(props, "parallel.max-threads", config.maxParallelDownloads)
                    config.cleanupIntervalHours = parseIntProperty(props, "cleanup.interval-hours", config.cleanupIntervalHours)
                    config.maxFileAgeHours = parseIntProperty(props, "cleanup.file-max-age-hours", config.maxFileAgeHours)
                }
            } catch (exception: Exception) {
                System.err.println("Failed to load dependency configuration from $configFile: ${exception.message}")
            }

            return config
        }

        private fun loadProperty(key: String, defaultValue: String): String {
            val systemProperty = System.getProperty(key)
            if (!systemProperty.isNullOrBlank()) {
                return systemProperty.trim()
            }

            val envKey = key.replace('.', '_').uppercase()
            val envValue = System.getenv(envKey)
            if (!envValue.isNullOrBlank()) {
                return envValue.trim()
            }
            return defaultValue
        }

        private fun loadIntProperty(key: String, defaultValue: Int): Int {
            return loadProperty(key, defaultValue.toString()).toIntOrNull() ?: defaultValue
        }

        private fun loadBoolProperty(key: String, defaultValue: Boolean): Boolean {
            return loadProperty(key, defaultValue.toString()).toBoolean()
        }

        private fun parseIntProperty(props: Properties, key: String, defaultValue: Int): Int {
            return props.getProperty(key)?.trim()?.takeUnless(String::isEmpty)?.toIntOrNull() ?: defaultValue
        }

        private fun parseBoolProperty(props: Properties, key: String, defaultValue: Boolean): Boolean {
            return props.getProperty(key)?.trim()?.takeUnless(String::isEmpty)?.toBoolean() ?: defaultValue
        }
    }
}
