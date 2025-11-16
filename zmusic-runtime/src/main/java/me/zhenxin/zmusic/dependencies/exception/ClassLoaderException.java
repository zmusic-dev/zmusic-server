package me.zhenxin.zmusic.dependencies.exception;

import java.io.File;

/**
 * ClassLoader 操作异常
 * 当 JAR 文件注入到 ClassLoader 失败时抛出
 *
 * @author ZMusic Team
 * @since 4.0.0
 */
public class ClassLoaderException extends DependencyException {

    private final File jarFile;
    private final String classLoaderType;

    /**
     * 创建 ClassLoader 异常
     *
     * @param jarFile         JAR 文件
     * @param classLoaderType ClassLoader 类型
     * @param message         异常消息
     */
    public ClassLoaderException(File jarFile, String classLoaderType, String message) {
        super("Failed to inject '" + jarFile.getName() + "' into " + classLoaderType + ": " + message);
        this.jarFile = jarFile;
        this.classLoaderType = classLoaderType;
    }

    /**
     * 创建 ClassLoader 异常（带原因）
     *
     * @param jarFile         JAR 文件
     * @param classLoaderType ClassLoader 类型
     * @param message         异常消息
     * @param cause           原始异常
     */
    public ClassLoaderException(File jarFile, String classLoaderType, String message, Throwable cause) {
        super("Failed to inject '" + jarFile.getName() + "' into " + classLoaderType + ": " + message, cause);
        this.jarFile = jarFile;
        this.classLoaderType = classLoaderType;
    }

    /**
     * 获取失败的 JAR 文件
     */
    public File getJarFile() {
        return jarFile;
    }

    /**
     * 获取 ClassLoader 类型
     */
    public String getClassLoaderType() {
        return classLoaderType;
    }
}
