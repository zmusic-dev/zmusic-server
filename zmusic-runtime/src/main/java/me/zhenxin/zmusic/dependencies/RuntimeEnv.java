package me.zhenxin.zmusic.dependencies;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static me.zhenxin.zmusic.ZMusicConstants.KOTLIN_VERSION;

/**
 * TabooLib
 * taboolib.common.env.RuntimeEnv
 *
 * @author sky
 * @since 2021/6/15 6:23 下午
 */
public class RuntimeEnv {
    public static final RuntimeEnv ENV = new RuntimeEnv();
    public static final RuntimeEnvDependency ENV_DEPENDENCY = new RuntimeEnvDependency();

    public void runtimeInit(String dataFolder) {
        // 数据目录是否存在
        File dataFolderFile = new File(dataFolder);
        if (!dataFolderFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            dataFolderFile.mkdirs();
        }
        // 设置默认库路径
        String defaultLibrary = ENV_DEPENDENCY.getDefaultLibrary();
        ENV_DEPENDENCY.setDefaultLibrary(dataFolder + "/" + defaultLibrary);

        // 初始化JAR缓存管理器
        JarCacheManager.getInstance().initialize();

        // 加载 Kotlin 环境
        try {
            List<JarRelocation> relocations = new ArrayList<>();
            // Kotlin Relocation
            String kotlinId = "!kotlin".substring(1);
            String kotlinRelocationId = "!me.zhenxin.zmusic.library.kotlin".substring(1);
            relocations.add(new JarRelocation(kotlinId + ".", kotlinRelocationId + "."));
            // Kotlin Dependency
            String kotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib:" + KOTLIN_VERSION;
            String kotlinStdlibJdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:" + KOTLIN_VERSION;
            ENV_DEPENDENCY.loadDependency(kotlinStdlib, false, relocations);
            ENV_DEPENDENCY.loadDependency(kotlinStdlibJdk8, false, relocations);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void inject(@NotNull Class<?> clazz) throws Throwable {
        ENV_DEPENDENCY.loadDependency(clazz);
    }
}
