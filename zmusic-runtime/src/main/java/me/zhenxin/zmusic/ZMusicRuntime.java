package me.zhenxin.zmusic;

import me.zhenxin.zmusic.dependencies.RuntimeDependency;
import me.zhenxin.zmusic.dependencies.RuntimeEnv;
import me.zhenxin.zmusic.dependencies.common.RuntimeLogger;

import static me.zhenxin.zmusic.dependencies.common.PrimitiveIO.t;

/**
 * ZMusic 运行时依赖
 *
 * @author 真心
 * @since 2023/7/24 18:14
 */
@SuppressWarnings({"AlibabaClassNamingShouldBeCamel", "SpellCheckingInspection"})
@RuntimeDependency(
        value = "!org.dromara.hutool:hutool-core:" + ZMusicConstants.HUTOOL_VERSION,
        test = "!org.dromara.hutool.core.text.StrUtil"
)
@RuntimeDependency(
        value = "!org.dromara.hutool:hutool-http:" + ZMusicConstants.HUTOOL_VERSION,
        test = "!org.dromara.hutool.http.HttpUtil"
)
@RuntimeDependency(
        value = "!org.dromara.hutool:hutool-log:" + ZMusicConstants.HUTOOL_VERSION,
        test = "!org.dromara.hutool.log.LogUtil"
)
@RuntimeDependency(
        value = "!org.dromara.hutool:hutool-json:" + ZMusicConstants.HUTOOL_VERSION,
        test = "!org.dromara.hutool.json.JSONUtil"
)
@RuntimeDependency(
        value = "!io.netty:netty-buffer:" + ZMusicConstants.NETTY_VERSION,
        test = "!io.netty.buffer.ByteBuf"
)
@RuntimeDependency(
        value = "!com.electronwill.night-config:core:" + ZMusicConstants.NIGHT_CONFIG_VERSION,
        test = "!com.electronwill.nightconfig.core.Config"
)
@RuntimeDependency(
        value = "!com.electronwill.night-config:toml:" + ZMusicConstants.NIGHT_CONFIG_VERSION,
        test = "!com.electronwill.nightconfig.toml.TomlFormat"
)
@RuntimeDependency(
        value = "!org.bstats:bstats-base:" + ZMusicConstants.BSTATS_VERSION,
        test = "!me.zhenxin.zmusic.library.bstats.MetricsBase",
        relocate = {"!org.bstats.", "!me.zhenxin.zmusic.library.bstats."}
)
public class ZMusicRuntime {

    public static void setup(String dataFolder, Class<?>... classes) {
        // 目录检测
        RuntimeLogger.info(t(
                "正在初始化运行时依赖，请稍等...",
                "Initializing runtime dependencies, please wait..."
        ));
        RuntimeEnv.ENV.runtimeInit(dataFolder);
        try {
            RuntimeEnv.ENV.inject(ZMusicRuntime.class);
            for (Class<?> clazz : classes) {
                RuntimeEnv.ENV.inject(clazz);
            }
        } catch (Throwable t) {
            RuntimeLogger.warning(t(
                    "加载运行时依赖失败，请检查运行环境！",
                    "Failed to load runtime dependencies, please check the runtime environment!"
            ));
            t.printStackTrace();
        }
        RuntimeLogger.info(t(
                "运行时依赖加载完成！",
                "Runtime dependencies loaded!"
        ));
    }
}
