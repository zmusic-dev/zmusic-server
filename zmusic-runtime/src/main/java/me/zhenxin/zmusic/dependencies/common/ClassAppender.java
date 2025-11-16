package me.zhenxin.zmusic.dependencies.common;

import me.zhenxin.zmusic.ZMusicRuntime;
import me.zhenxin.zmusic.dependencies.exception.ClassLoaderException;
import sun.misc.Unsafe;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static me.zhenxin.zmusic.dependencies.common.PrimitiveIO.t;

/**
 * ClassLoader 操作工具类
 * 支持 Java 8-21 的多版本兼容性
 *
 * @author sky
 * @since 2020-04-12 22:39
 */
public class ClassAppender {

    final static List<Callback> CALLBACKS = new ArrayList<>();
    static MethodHandles.Lookup lookup;
    static Unsafe unsafe;
    static boolean useReflectionFallback = false;

    static {
        // 所有 Java 版本统一使用 Unsafe 方案（避免模块化问题）
        initLookupJava8();

        // 输出版本信息（仅在详细模式下）
        if (System.getProperty("zmusic.dependency.verbose", "false").equalsIgnoreCase("true")) {
            RuntimeLogger.info(t(
                    "ClassAppender 初始化完成 | " + JavaVersionDetector.getDebugInfo() +
                            " | Lookup: " + (lookup != null ? "成功" : "失败") +
                            " | 降级模式: " + useReflectionFallback,
                    "ClassAppender initialized | " + JavaVersionDetector.getDebugInfo() +
                            " | Lookup: " + (lookup != null ? "Success" : "Failed") +
                            " | Fallback mode: " + useReflectionFallback
            ));
        }
    }

    /**
     * Java 8 初始化 Lookup（使用 Unsafe）
     */
    private static void initLookupJava8() {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
            Field lookupField = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            Object lookupBase = unsafe.staticFieldBase(lookupField);
            long lookupOffset = unsafe.staticFieldOffset(lookupField);
            lookup = (MethodHandles.Lookup) unsafe.getObject(lookupBase, lookupOffset);

            if (lookup == null) {
                RuntimeLogger.warning(t(
                        "Java 8 Unsafe lookup 初始化失败，尝试降级方案",
                        "Java 8 Unsafe lookup initialization failed, trying fallback"
                ));
                initLookupFallback();
            }
        } catch (Throwable e) {
            RuntimeLogger.warning(t(
                    "Java 8 Unsafe 访问失败: " + e.getMessage() + "，尝试降级方案",
                    "Java 8 Unsafe access failed: " + e.getMessage() + ", trying fallback"
            ));
            initLookupFallback();
        }
    }

    /**
     * Java 9-11 初始化 Lookup（尝试新 API，失败则降级）
     */
    private static void initLookupJava9To11() {
        try {
            // Java 9+ 推荐使用 MethodHandles.privateLookupIn()
            // 但在 Java 9-11 可能还需要添加 --add-opens
            Method privateLookupIn = MethodHandles.class.getMethod(
                    "privateLookupIn",
                    Class.class,
                    MethodHandles.Lookup.class
            );
            lookup = (MethodHandles.Lookup) privateLookupIn.invoke(
                    null,
                    MethodHandles.Lookup.class,
                    MethodHandles.lookup()
            );

            if (lookup == null) {
                throw new IllegalStateException("privateLookupIn returned null");
            }
        } catch (Throwable e) {
            // 获取真实的错误信息（InvocationTargetException 的 cause）
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            String errorMsg = cause.getMessage() != null ? cause.getMessage() : cause.getClass().getSimpleName();

            // 模块化限制是预期行为，使用 debug 级别而非 warning
            RuntimeLogger.debug(t(
                    "Java 9-11 privateLookupIn 需要模块访问权限: " + errorMsg + "，降级使用 Unsafe 方案",
                    "Java 9-11 privateLookupIn requires module access: " + errorMsg + ", fallback to Unsafe"
            ));
            // 降级到 Java 8 的 Unsafe 方式
            initLookupJava8();
        }
    }

    /**
     * Java 12+ 初始化 Lookup（优先使用标准 API）
     */
    private static void initLookupJava12Plus() {
        try {
            // Java 9+ 标准方式（使用反射以兼容 Java 8 编译）
            Method privateLookupIn = MethodHandles.class.getMethod(
                    "privateLookupIn",
                    Class.class,
                    MethodHandles.Lookup.class
            );
            lookup = (MethodHandles.Lookup) privateLookupIn.invoke(
                    null,
                    MethodHandles.Lookup.class,
                    MethodHandles.lookup()
            );

            if (lookup == null) {
                throw new IllegalStateException("privateLookupIn returned null");
            }
        } catch (Throwable e) {
            // 获取真实的错误信息（InvocationTargetException 的 cause）
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            String errorMsg = cause.getMessage() != null ? cause.getMessage() : cause.getClass().getSimpleName();

            // 模块化限制是预期行为，使用 debug 级别而非 warning
            RuntimeLogger.debug(t(
                    "Java 12+ privateLookupIn 需要模块访问权限: " + errorMsg + "，降级使用 Unsafe 方案",
                    "Java 12+ privateLookupIn requires module access: " + errorMsg + ", fallback to Unsafe"
            ));
            // 尝试 Unsafe（某些环境可能仍然可用）
            initLookupJava8();
        }
    }

    /**
     * 降级方案：使用传统反射而非 MethodHandle
     */
    private static void initLookupFallback() {
        useReflectionFallback = true;
        RuntimeLogger.info(t(
                "ClassAppender 启用反射降级模式（性能可能稍低）",
                "ClassAppender enabled reflection fallback mode (performance may be slightly lower)"
        ));
    }

    /**
     * 加载一个文件到 ClassLoader
     *
     * @param path       路径
     * @param isExternal 是否外部库（不加入 loadedClasses）
     * @throws ClassLoaderException 如果注入失败
     */
    public static ClassLoader addPath(Path path, boolean isExternal) throws ClassLoaderException {
        File file = new File(path.toUri().getPath());
        ClassLoader loader = ZMusicRuntime.class.getClassLoader();
        String loaderType = loader.getClass().getSimpleName();

        try {
            // Application
            if ("AppClassLoader".equals(loaderType)) {
                addUrl(loader, ucp(loader.getClass()), file, isExternal);
            }
            // Hybrid
            else if ("net.minecraft.launchwrapper.LaunchClassLoader".equals(loader.getClass().getName())) {
                if (lookup == null) {
                    throw new ClassLoaderException(file, loaderType, "MethodHandles.Lookup not initialized");
                }
                MethodHandle methodHandle = lookup.findVirtual(URLClassLoader.class, "addURL", MethodType.methodType(void.class, java.net.URL.class));
                methodHandle.invoke(loader, file.toURI().toURL());
            }
            // Bukkit
            else {
                addUrl(loader, ucp(loader), file, isExternal);
            }
            return loader;
        } catch (ClassLoaderException e) {
            throw e; // 直接抛出
        } catch (Throwable e) {
            throw new ClassLoaderException(file, loaderType, "Unexpected error during injection", e);
        }
    }

    /**
     * 获取 addPath 函数所使用的 ClassLoader（原函数为：judgeAddPathClassLoader）
     */
    public static ClassLoader getClassLoader() {
        return ZMusicRuntime.class.getClassLoader();
    }

    /**
     * 判断类是否存在
     */
    public static boolean isExists(String path) {
        try {
            Class.forName(path, false, getClassLoader());
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    private static void addUrl(ClassLoader loader, Field ucpField, File file, boolean isExternal) throws ClassLoaderException {
        String loaderType = loader.getClass().getName();

        if (ucpField == null) {
            throw new ClassLoaderException(file, loaderType, "URLClassPath field not found");
        }

        // 使用降级模式（传统反射）
        if (useReflectionFallback || lookup == null) {
            addUrlReflection(loader, ucpField, file, isExternal);
            return;
        }

        // 使用 MethodHandle 模式（高性能）
        if (unsafe == null) {
            throw new ClassLoaderException(file, loaderType, "Unsafe not initialized");
        }

        try {
            Object ucp = unsafe.getObject(loader, unsafe.objectFieldOffset(ucpField));
            MethodHandle methodHandle = lookup.findVirtual(ucp.getClass(), "addURL", MethodType.methodType(void.class, URL.class));
            methodHandle.invoke(ucp, file.toURI().toURL());
            for (Callback i : CALLBACKS) {
                i.add(loader, file, isExternal);
            }
        } catch (NoSuchMethodError e) {
            throw new ClassLoaderException(file, loaderType, "addURL method not found", e);
        } catch (Throwable e) {
            throw new ClassLoaderException(file, loaderType, "Failed to invoke addURL", e);
        }
    }

    /**
     * 使用传统反射方式添加 URL（降级方案）
     */
    private static void addUrlReflection(ClassLoader loader, Field ucpField, File file, boolean isExternal) throws ClassLoaderException {
        String loaderType = loader.getClass().getName();
        try {
            // 获取 ucp 对象
            ucpField.setAccessible(true);
            Object ucp = ucpField.get(loader);

            // 获取 addURL 方法
            Method addURLMethod = ucp.getClass().getDeclaredMethod("addURL", URL.class);
            addURLMethod.setAccessible(true);

            // 调用方法
            addURLMethod.invoke(ucp, file.toURI().toURL());

            // 触发回调
            for (Callback i : CALLBACKS) {
                i.add(loader, file, isExternal);
            }
        } catch (Exception e) {
            throw new ClassLoaderException(file, loaderType, "Reflection fallback failed", e);
        }
    }

    private static Field ucp(ClassLoader loader) {
        try {
            return URLClassLoader.class.getDeclaredField("ucp");
        } catch (NoSuchFieldError | NoSuchFieldException ignored) {
            return ucp(loader.getClass());
        }
    }

    private static Field ucp(Class<?> loader) {
        try {
            return loader.getDeclaredField("ucp");
        } catch (NoSuchFieldError | NoSuchFieldException e2) {
            Class<?> superclass = loader.getSuperclass();
            if (superclass == Object.class) {
                return null;
            }
            return ucp(superclass);
        }
    }

    public static void registerCallback(Callback callback) {
        CALLBACKS.add(callback);
    }

    public interface Callback {

        void add(ClassLoader loader, File file, boolean isExternal);
    }
}