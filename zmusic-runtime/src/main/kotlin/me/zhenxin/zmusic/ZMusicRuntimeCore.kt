package me.zhenxin.zmusic

import me.zhenxin.zmusic.dependencies.RuntimeDependency
import me.zhenxin.zmusic.dependencies.RuntimeEnv
import me.zhenxin.zmusic.dependencies.common.RuntimeLogger
import java.io.PrintWriter
import java.io.StringWriter
import me.zhenxin.zmusic.dependencies.common.PrimitiveIO.t

@RuntimeDependency(
    value = "!cn.hutool:hutool-core:${ZMusicConstants.HUTOOL_VERSION}",
    test = "!cn.hutool.core.text.StrUtil"
)
@RuntimeDependency(
    value = "!cn.hutool:hutool-http:${ZMusicConstants.HUTOOL_VERSION}",
    test = "!cn.hutool.http.HttpUtil"
)
@RuntimeDependency(
    value = "!cn.hutool:hutool-log:${ZMusicConstants.HUTOOL_VERSION}",
    test = "!cn.hutool.log.LogUtil"
)
@RuntimeDependency(
    value = "!cn.hutool:hutool-json:${ZMusicConstants.HUTOOL_VERSION}",
    test = "!cn.hutool.json.JSONUtil"
)
@RuntimeDependency(
    value = "!io.netty:netty-buffer:${ZMusicConstants.NETTY_VERSION}",
    test = "!io.netty.buffer.ByteBuf"
)
@RuntimeDependency(
    value = "!com.electronwill.night-config:core:${ZMusicConstants.NIGHT_CONFIG_VERSION}",
    test = "!com.electronwill.nightconfig.core.Config"
)
@RuntimeDependency(
    value = "!com.electronwill.night-config:toml:${ZMusicConstants.NIGHT_CONFIG_VERSION}",
    test = "!com.electronwill.nightconfig.toml.TomlFormat"
)
@RuntimeDependency(
    value = "!net.kyori:adventure-api:${ZMusicConstants.ADVENTURE_VERSION}",
    test = "!net.kyori.adventure.text.Component"
)
@RuntimeDependency(
    value = "!net.kyori:adventure-text-serializer-legacy:${ZMusicConstants.ADVENTURE_VERSION}",
    test = "!net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer"
)
object ZMusicRuntimeCore {

    @JvmStatic
    fun setup(dataFolder: String, classes: Array<Class<*>>) {
        RuntimeLogger.info(
            t(
                "正在初始化运行时依赖，请稍等...",
                "Initializing runtime dependencies, please wait..."
            )
        )

        RuntimeEnv.ENV.runtimeInit(dataFolder)

        try {
            RuntimeEnv.ENV.inject(ZMusicRuntimeCore::class.java)
            classes.forEach(RuntimeEnv.ENV::inject)
        } catch (throwable: Throwable) {
            RuntimeLogger.warning(
                t(
                    "加载运行时依赖失败，请检查运行环境！{0}",
                    "Failed to load runtime dependencies, please check the runtime environment! {0}"
                ),
                throwable.message
            )
            RuntimeLogger.debug(
                t(
                    "异常堆栈: {0}",
                    "Exception stack trace: {0}"
                ),
                stackTraceOf(throwable)
            )
        }

        RuntimeLogger.info(
            t(
                "运行时依赖加载完成！",
                "Runtime dependencies loaded!"
            )
        )
        RuntimeLogger.logDependencyStats()
    }

    private fun stackTraceOf(throwable: Throwable): String {
        val stringWriter = StringWriter()
        PrintWriter(stringWriter).use { writer ->
            throwable.printStackTrace(writer)
        }
        return stringWriter.toString()
    }
}
