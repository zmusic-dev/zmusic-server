package me.zhenxin.zmusic;

import me.zhenxin.zmusic.dependencies.KotlinBootstrapLoader;
import me.zhenxin.zmusic.dependencies.common.BootstrapContext;
import java.lang.reflect.Method;

/**
 * ZMusic Java Bootstrap。
 * 仅负责加载 Kotlin 运行时，然后将后续初始化委托给 Kotlin 入口。
 *
 * @author 真心
 * @since 2023/7/24 18:14
 */
public class ZMusicRuntime {

    private static final String KOTLIN_RUNTIME_CLASS = "me.zhenxin.zmusic.ZMusicRuntimeCore";

    public static void setup(String dataFolder) { runRuntimePhase(() -> KotlinBootstrapLoader.bootstrap(dataFolder)); }

    public static void initialize(String dataFolder, Class<?>... classes) { runRuntimePhase(() -> invokeKotlinRuntime(dataFolder, classes)); }

    private static void invokeKotlinRuntime(String dataFolder, Class<?>[] classes) throws Throwable {
        Class<?> runtimeCoreClass = Class.forName(KOTLIN_RUNTIME_CLASS, true, ZMusicRuntime.class.getClassLoader());
        Method setupMethod = runtimeCoreClass.getDeclaredMethod("setup", String.class, Class[].class);
        setupMethod.invoke(null, dataFolder, (Object) classes);
    }

    private static void runRuntimePhase(ThrowingRunnable runnable) {
        try {
            runnable.run();
        } catch (Throwable t) {
            BootstrapContext.warning(BootstrapContext.t(
                    "加载运行时依赖失败，请检查运行环境！{0}",
                    "Failed to load runtime dependencies, please check the runtime environment! {0}"
            ), t.getMessage());
            BootstrapContext.debug(BootstrapContext.t(
                    "异常堆栈: {0}",
                    "Exception stack trace: {0}"
            ), BootstrapContext.stackTraceOf(t));
        }
    }

    @FunctionalInterface
    private interface ThrowingRunnable {

        void run() throws Throwable;
    }
}
