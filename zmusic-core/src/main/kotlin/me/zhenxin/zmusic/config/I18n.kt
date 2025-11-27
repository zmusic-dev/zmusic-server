package me.zhenxin.zmusic.config

import com.electronwill.nightconfig.core.CommentedConfig
import com.electronwill.nightconfig.toml.TomlParser
import me.zhenxin.zmusic.ZMusic
import me.zhenxin.zmusic.logger
import java.util.*

/**
 * 国际化
 *
 * @author 真心
 * @since 2024/2/7 9:19
 */
object I18n {
    object Platform {
        val netease: String
            get() = getI18nValue("platform.netease")

        val bilibili: String
            get() = getI18nValue("platform.bilibili")
    }

    object Init {
        val loaded: List<String>
            get() = getI18nListValue("init.loaded")
    }

    object Disable {
        val disabled: String
            get() = getI18nValue("disable.disabled")
    }

    object Update {
        val checking: String
            get() = getI18nValue("update.checking")

        val checkFailed: String
            get() = getI18nValue("update.check_failed")

        val available: List<String>
            get() = getI18nListValue("update.available")

        val notAvailable: String
            get() = getI18nValue("update.not_available")
    }

    object Help {
        val tips: String
            get() = getI18nValue("help.tips")
    }

    object Config {
        val outdated: String
            get() = getI18nValue("config.outdated")
    }
}

/**
 * 初始化国际化配置
 */
fun initI18n() {
    val supportedLanguages = arrayOf("en-US", "zh-CN", "ja-JP")
    val classLoader = ZMusic::class.java.classLoader
    val parser = TomlParser()

    // 加载英文作为默认回退配置
    val fallbackFile = classLoader.getResource("i18n/en-US.toml")
        ?: run {
            // 此时无法使用国际化，保持英文硬编码
            logger.error("Failed to load fallback language file (en-US.toml), please check your plugin jar.")
            return
        }
    fallbackI18n = parser.parse(fallbackFile)

    // 解析用户配置的语言
    var language = Config.language
    if (language == "auto") {
        val locale = Locale.getDefault()
        language = "${locale.language}-${locale.country}"
    }

    // 加载目标语言配置
    i18n = when (language) {
        "en-US" -> fallbackI18n
        in supportedLanguages -> {
            val langFile = classLoader.getResource("i18n/$language.toml")
            if (langFile != null) {
                parser.parse(langFile)
            } else {
                // 加载失败时保持英文硬编码，避免循环依赖
                logger.warn("Failed to load $language.toml, fallback to en-US.")
                fallbackI18n
            }
        }

        else -> {
            // 不支持的语言，保持英文硬编码
            logger.warn("Language $language is not supported, use default language en-US. Supported: ${supportedLanguages.joinToString()}")
            fallbackI18n
        }
    }
}

private lateinit var i18n: CommentedConfig
private lateinit var fallbackI18n: CommentedConfig

/**
 * 获取国际化字符串配置，支持回退到英文
 * @param key 配置键
 * @return 配置值，如果当前语言和英文都未配置则返回占位符
 */
private fun getI18nValue(key: String): String {
    // 先尝试从当前语言获取
    val value: String? = i18n.get(key)
    if (value != null) {
        return value
    }

    // 如果当前语言和回退语言是同一个，返回占位符
    if (i18n === fallbackI18n) {
        return "{$key}"
    }

    // 如果当前语言未配置，尝试从回退语言获取
    logger.debug("Key '$key' not found in current language, fallback to en-US")
    return fallbackI18n.get(key) ?: "{$key}"
}

/**
 * 获取国际化列表配置，支持回退到英文
 * @param key 配置键
 * @return 配置值，如果当前语言和英文都未配置则返回包含占位符的列表
 */
private fun getI18nListValue(key: String): List<String> {
    // 先尝试从当前语言获取
    val value: List<String>? = i18n.get(key)
    if (value != null) {
        return value
    }

    // 如果当前语言和回退语言是同一个，返回占位符列表
    if (i18n === fallbackI18n) {
        return listOf("{$key}")
    }

    // 如果当前语言未配置，尝试从回退语言获取
    logger.debug("Key '$key' not found in current language, fallback to en-US")
    return fallbackI18n.get(key) ?: listOf("{$key}")
}