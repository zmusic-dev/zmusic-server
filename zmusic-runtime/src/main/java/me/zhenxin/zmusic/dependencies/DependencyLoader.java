package me.zhenxin.zmusic.dependencies;

import me.zhenxin.zmusic.dependencies.exception.DependencyException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 依赖加载器 - Builder 模式
 * 提供流畅的 API 来加载 Maven 依赖，替代 RuntimeEnvDependency 中的多个重载方法
 *
 * <p>使用示例：
 * <pre>{@code
 * new DependencyLoader()
 *     .coordinate("org.example:foo:1.0.0")
 *     .repository("https://repo.maven.apache.org/maven2/")
 *     .relocate("org.example", "com.myapp.libs.example")
 *     .transitive(true)
 *     .load();
 * }</pre>
 *
 * @author ZMusic Team
 * @since 4.0.0
 */
public class DependencyLoader {

    private final RuntimeEnvDependency dependency;

    // 必需参数
    private String coordinate;

    // 可选参数（带默认值）
    private File baseDirectory;
    private List<JarRelocation> relocations = new ArrayList<>();
    private String repository = null; // null 表示使用默认仓库
    private boolean ignoreOptional = true;
    private boolean ignoreException = false;
    private boolean transitive = true;
    private List<DependencyScope> scopes = Arrays.asList(DependencyScope.RUNTIME, DependencyScope.COMPILE);
    private boolean external = true;

    /**
     * 创建一个新的依赖加载器
     * 使用默认的 RuntimeEnvDependency 实例
     */
    public DependencyLoader() {
        this(RuntimeEnv.ENV_DEPENDENCY);
    }

    /**
     * 创建一个新的依赖加载器
     *
     * @param dependency RuntimeEnvDependency 实例
     */
    public DependencyLoader(@NotNull RuntimeEnvDependency dependency) {
        this.dependency = dependency;
        this.baseDirectory = new File(dependency.getDefaultLibrary());
    }

    /**
     * 设置 Maven 坐标（必需）
     *
     * @param coordinate Maven 坐标，格式: groupId:artifactId:version
     * @return 当前 Builder 实例
     */
    public DependencyLoader coordinate(@NotNull String coordinate) {
        this.coordinate = coordinate;
        return this;
    }

    /**
     * 设置基础目录（可选）
     * 默认使用 RuntimeEnvDependency 的默认库目录
     *
     * @param baseDirectory 依赖下载和缓存的基础目录
     * @return 当前 Builder 实例
     */
    public DependencyLoader baseDirectory(@NotNull File baseDirectory) {
        this.baseDirectory = baseDirectory;
        return this;
    }

    /**
     * 设置基础目录（可选）
     *
     * @param baseDirectory 依赖下载和缓存的基础目录路径
     * @return 当前 Builder 实例
     */
    public DependencyLoader baseDirectory(@NotNull String baseDirectory) {
        this.baseDirectory = new File(baseDirectory);
        return this;
    }

    /**
     * 添加 JAR 包重定向规则（可选）
     * 用于避免依赖冲突，将包名重定向到独立的命名空间
     *
     * @param from 原始包名（如 "org.example"）
     * @param to   目标包名（如 "com.myapp.libs.example"）
     * @return 当前 Builder 实例
     */
    public DependencyLoader relocate(@NotNull String from, @NotNull String to) {
        this.relocations.add(new JarRelocation(from, to));
        return this;
    }

    /**
     * 批量添加 JAR 包重定向规则（可选）
     *
     * @param relocations 重定向规则列表
     * @return 当前 Builder 实例
     */
    public DependencyLoader relocations(@NotNull List<JarRelocation> relocations) {
        this.relocations.addAll(relocations);
        return this;
    }

    /**
     * 设置 Maven 仓库 URL（可选）
     * 默认使用 DependencyConfig.DEFAULT_REPOSITORY
     *
     * @param repository Maven 仓库 URL
     * @return 当前 Builder 实例
     */
    public DependencyLoader repository(@NotNull String repository) {
        this.repository = repository;
        return this;
    }

    /**
     * 设置是否忽略可选依赖（可选）
     * 默认值: true
     *
     * @param ignoreOptional true 表示忽略 Maven POM 中的可选依赖
     * @return 当前 Builder 实例
     */
    public DependencyLoader ignoreOptional(boolean ignoreOptional) {
        this.ignoreOptional = ignoreOptional;
        return this;
    }

    /**
     * 设置是否忽略加载异常（可选）
     * 默认值: false
     *
     * @param ignoreException true 表示加载失败时不抛出异常
     * @return 当前 Builder 实例
     */
    public DependencyLoader ignoreException(boolean ignoreException) {
        this.ignoreException = ignoreException;
        return this;
    }

    /**
     * 设置是否加载传递依赖（可选）
     * 默认值: true
     *
     * @param transitive true 表示递归加载所有传递依赖
     * @return 当前 Builder 实例
     */
    public DependencyLoader transitive(boolean transitive) {
        this.transitive = transitive;
        return this;
    }

    /**
     * 设置依赖作用域过滤（可选）
     * 默认值: [RUNTIME, COMPILE]
     *
     * @param scopes 允许的依赖作用域列表
     * @return 当前 Builder 实例
     */
    public DependencyLoader scopes(@NotNull DependencyScope... scopes) {
        this.scopes = Arrays.asList(scopes);
        return this;
    }

    /**
     * 设置依赖作用域过滤（可选）
     *
     * @param scopes 允许的依赖作用域列表
     * @return 当前 Builder 实例
     */
    public DependencyLoader scopes(@NotNull List<DependencyScope> scopes) {
        this.scopes = scopes;
        return this;
    }

    /**
     * 设置是否为外部库（可选）
     * 默认值: true
     *
     * @param external false 表示此依赖会被添加到 loadedClasses 追踪
     * @return 当前 Builder 实例
     */
    public DependencyLoader external(boolean external) {
        this.external = external;
        return this;
    }

    /**
     * 加载依赖
     * 这是终结操作，会实际执行依赖的下载和注入
     *
     * @throws DependencyException 如果加载失败且 ignoreException 为 false
     * @throws IllegalStateException 如果未设置 coordinate
     */
    public void load() throws DependencyException {
        if (coordinate == null || coordinate.isEmpty()) {
            throw new IllegalStateException("Maven coordinate is required. Use .coordinate(\"groupId:artifactId:version\") to set it.");
        }

        // 调用核心加载方法（包装 Throwable 为 DependencyException）
        try {
            dependency.loadDependency(
                    coordinate,
                    baseDirectory,
                    relocations,
                    repository,
                    ignoreOptional,
                    ignoreException,
                    transitive,
                    scopes,
                    external
            );
        } catch (DependencyException e) {
            throw e; // 直接抛出 DependencyException
        } catch (Throwable e) {
            // 包装其他异常为 DependencyException
            throw new DependencyException("Failed to load dependency: " + coordinate, e);
        }
    }

    /**
     * 验证构建器的参数（内部使用）
     */
    private void validate() {
        if (coordinate == null || coordinate.isEmpty()) {
            throw new IllegalStateException("Maven coordinate is required");
        }
        if (baseDirectory == null) {
            throw new IllegalStateException("Base directory cannot be null");
        }
        if (scopes == null || scopes.isEmpty()) {
            throw new IllegalStateException("Dependency scopes cannot be empty");
        }
    }

    /**
     * 获取当前配置的摘要信息（用于调试）
     *
     * @return 配置信息字符串
     */
    @Override
    public String toString() {
        return "DependencyLoader{" +
                "coordinate='" + coordinate + '\'' +
                ", baseDirectory=" + baseDirectory +
                ", relocations=" + relocations.size() +
                ", repository='" + (repository != null ? repository : "default") + '\'' +
                ", ignoreOptional=" + ignoreOptional +
                ", ignoreException=" + ignoreException +
                ", transitive=" + transitive +
                ", scopes=" + scopes +
                ", external=" + external +
                '}';
    }
}
