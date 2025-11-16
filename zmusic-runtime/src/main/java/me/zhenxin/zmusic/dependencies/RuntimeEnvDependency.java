package me.zhenxin.zmusic.dependencies;

import me.zhenxin.zmusic.dependencies.aether.AetherResolver;
import me.zhenxin.zmusic.dependencies.common.ClassAppender;
import me.zhenxin.zmusic.dependencies.common.PrimitiveIO;
import me.zhenxin.zmusic.dependencies.legacy.Artifact;
import me.zhenxin.zmusic.dependencies.legacy.Dependency;
import me.zhenxin.zmusic.dependencies.legacy.DependencyDownloader;
import me.zhenxin.zmusic.dependencies.legacy.Repository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static me.zhenxin.zmusic.dependencies.common.PrimitiveIO.t;


public class RuntimeEnvDependency {

    private static boolean isAetherFound;

    static {
        // 当服务端版本在 1.17+ 时，可借助服务端自带的 Aether 库完成依赖下载，兼容性更高。
        // 同时停止对 Legacy 的支持。
        try {
            Class.forName("org.eclipse.aether.graph.Dependency");
            isAetherFound = true;
        } catch (ClassNotFoundException e) {
            isAetherFound = false;
        }
        // Mohist 直接不用 Aether
        try {
            Class.forName("com.mohistmc.MohistMC");
            isAetherFound = false;
        } catch (ClassNotFoundException ignored) {
        }
    }

    private String defaultLibrary = DependencyConfig.DEFAULT_LIBRARY_DIR;

    public String getDefaultLibrary() {
        return defaultLibrary;
    }

    public void setDefaultLibrary(String library) {
        this.defaultLibrary = library;
    }

    public ParsedDependency parseDependency(RuntimeDependency dependency) {
        String value = dependency.value();
        String test = dependency.test();
        String repository = dependency.repository();
        boolean transitive = dependency.transitive();
        boolean ignoreOptional = dependency.ignoreOptional();
        boolean ignoreException = dependency.ignoreException();
        boolean external = dependency.external();
        List<DependencyScope> scopes = new ArrayList<>(Arrays.asList(dependency.scopes()));
        List<String> relocate = new ArrayList<>(Arrays.asList(dependency.relocate()));
        return new ParsedDependency(value, test, repository, transitive, ignoreOptional, ignoreException, scopes, relocate, external);
    }

    public List<ParsedDependency> getDependency(@NotNull Class<?> clazz) {
        List<ParsedDependency> dependencyList = new ArrayList<>();
        RuntimeDependency[] dependencies = null;
        if (clazz.isAnnotationPresent(RuntimeDependency.class)) {
            dependencies = clazz.getAnnotationsByType(RuntimeDependency.class);
        } else {
            RuntimeDependencies annotation = clazz.getAnnotation(RuntimeDependencies.class);
            if (annotation != null) {
                dependencies = annotation.value();
            }
        }
        if (dependencies != null) {
            for (RuntimeDependency dependency : dependencies) {
                ParsedDependency parsedDependency = parseDependency(dependency);
                if (parsedDependency != null) {
                    dependencyList.add(parsedDependency);
                }
            }
            return dependencyList;
        } else {
            return Collections.emptyList();
        }
    }

    public void loadDependency(@NotNull Class<?> clazz) throws Throwable {
        List<ParsedDependency> dependencies = getDependency(clazz);
        if (dependencies != null) {
            File baseFile = new File(defaultLibrary);
            for (ParsedDependency dep : dependencies) {
                String allTest = dep.test();
                List<String> tests = new ArrayList<>();
                if (allTest.contains(",")) {
                    tests.addAll(Arrays.asList(allTest.split(",")));
                } else {
                    tests.add(allTest);
                }
                if (!tests.isEmpty() && tests.stream().allMatch(this::test)) {
                    continue;
                }
                List<JarRelocation> relocation = new ArrayList<>();
                List<String> relocate = dep.relocate();
                if (relocate.size() % 2 != 0) {
                    throw new IllegalStateException("invalid relocate format");
                }
                for (int i = 0; i + 1 < relocate.size(); i += 2) {
                    String from = relocate.get(i);
                    String to = relocate.get(i + 1);
                    // 移除前缀
                    if (from.startsWith("!")) {
                        from = from.substring(1);
                    }
                    if (to.startsWith("!")) {
                        to = to.substring(1);
                    }
                    relocation.add(new JarRelocation(from, to));
                }
                String url = dep.value().startsWith("!") ? dep.value().substring(1) : dep.value();
                loadDependency(url, baseFile, relocation, dep.repository(), dep.ignoreOptional(), dep.ignoreException(), dep.transitive(), dep.scopes(), dep.external());
            }
        }
    }

    /**
     * 加载依赖 - 简化版
     *
     * @param url Maven 坐标
     * @throws Throwable 加载失败
     * @deprecated 使用 {@link DependencyLoader} 替代，提供更清晰的 Builder API
     * <pre>{@code
     * new DependencyLoader().coordinate("groupId:artifactId:version").load();
     * }</pre>
     */
    @Deprecated
    public void loadDependency(@NotNull String url) throws Throwable {
        loadDependency(url, new File(defaultLibrary));
    }

    /**
     * 加载依赖 - 带仓库
     *
     * @param url        Maven 坐标
     * @param repository 仓库 URL
     * @throws Throwable 加载失败
     * @deprecated 使用 {@link DependencyLoader} 替代
     * <pre>{@code
     * new DependencyLoader()
     *     .coordinate("...")
     *     .repository("...")
     *     .load();
     * }</pre>
     */
    @Deprecated
    public void loadDependency(@NotNull String url, @Nullable String repository) throws Throwable {
        loadDependency(url, new File(defaultLibrary), repository);
    }

    /**
     * 加载依赖 - 带重定向
     *
     * @param url        Maven 坐标
     * @param relocation 重定向规则
     * @throws Throwable 加载失败
     * @deprecated 使用 {@link DependencyLoader} 替代
     */
    @Deprecated
    public void loadDependency(@NotNull String url, @NotNull List<JarRelocation> relocation) throws Throwable {
        loadDependency(url, new File(defaultLibrary), relocation, null, true, false, true, Arrays.asList(DependencyScope.RUNTIME, DependencyScope.COMPILE));
    }

    /**
     * @deprecated 使用 {@link DependencyLoader} 替代
     */
    @Deprecated
    public void loadDependency(@NotNull String url, @NotNull List<JarRelocation> relocation, @Nullable String repository) throws Throwable {
        loadDependency(url, new File(defaultLibrary), relocation, repository, true, false, true, Arrays.asList(DependencyScope.RUNTIME, DependencyScope.COMPILE));
    }

    /**
     * @deprecated 使用 {@link DependencyLoader} 替代
     */
    @Deprecated
    public void loadDependency(@NotNull String url, boolean transitive, @NotNull List<JarRelocation> relocation) throws Throwable {
        loadDependency(url, new File(defaultLibrary), relocation, null, true, false, transitive, Arrays.asList(DependencyScope.RUNTIME, DependencyScope.COMPILE));
    }

    /**
     * @deprecated 使用 {@link DependencyLoader} 替代
     */
    @Deprecated
    public void loadDependency(@NotNull String url, boolean transitive, @NotNull List<JarRelocation> relocation, @Nullable String repository) throws Throwable {
        loadDependency(url, new File(defaultLibrary), relocation, repository, true, false, transitive, Arrays.asList(DependencyScope.RUNTIME, DependencyScope.COMPILE));
    }

    /**
     * @deprecated 使用 {@link DependencyLoader} 替代
     */
    @Deprecated
    public void loadDependency(@NotNull String url, @NotNull File baseDir) throws Throwable {
        loadDependency(url, baseDir, null);
    }

    /**
     * @deprecated 使用 {@link DependencyLoader} 替代
     */
    @Deprecated
    public void loadDependency(@NotNull String url, @NotNull File baseDir, @Nullable String repository) throws Throwable {
        loadDependency(url, baseDir, new ArrayList<>(), repository, true, false, true, Arrays.asList(DependencyScope.RUNTIME, DependencyScope.COMPILE));
    }

    /**
     * @deprecated 使用 {@link DependencyLoader} 替代
     */
    @Deprecated
    public void loadDependency(
            @NotNull String url,
            @NotNull File baseDir,
            @NotNull List<JarRelocation> relocation,
            @Nullable String repository,
            boolean ignoreOptional,
            boolean ignoreException,
            boolean transitive,
            @NotNull List<DependencyScope> scope
    ) throws Throwable {
        loadDependency(url, baseDir, relocation, repository, ignoreOptional, ignoreException, transitive, scope, true);
    }

    public void loadDependency(
            @NotNull String url,
            @NotNull File baseDir,
            @NotNull List<JarRelocation> relocation,
            @Nullable String repository,
            boolean ignoreOptional,
            boolean ignoreException,
            boolean transitive,
            @NotNull List<DependencyScope> scope,
            boolean external
    ) throws Throwable {
        if (repository == null || repository.isEmpty()) {
            repository = DependencyConfig.DEFAULT_REPOSITORY;
        }
        // 使用 Aether 处理依赖
        if (isAetherFound) {
            long startTime = me.zhenxin.zmusic.dependencies.common.RuntimeLogger.logDependencyLoadStart(url);
            try {
                List<File> resolvedFiles = AetherResolver.of(repository).resolve(url, scope, transitive, ignoreOptional);

                // 根据配置决定是否使用并行注入（仅对多个文件有意义）
                me.zhenxin.zmusic.dependencies.common.RuntimeLogger.debug(t("开始注入 {0} 个依赖文件", "Starting to inject {0} dependency files"), resolvedFiles.size());
                if (DependencyConfig.PARALLEL_DOWNLOAD && resolvedFiles.size() > 1) {
                    me.zhenxin.zmusic.dependencies.common.RuntimeLogger.debug(t("使用并行模式注入依赖", "Using parallel mode to inject dependencies"));
                    injectFilesParallel(resolvedFiles, relocation, external, ignoreException);
                } else {
                    me.zhenxin.zmusic.dependencies.common.RuntimeLogger.debug(t("使用顺序模式注入依赖", "Using sequential mode to inject dependencies"));
                    injectFilesSequential(resolvedFiles, relocation, external, ignoreException);
                }

                me.zhenxin.zmusic.dependencies.common.RuntimeLogger.logDependencyLoadSuccess(url, startTime);
            } catch (Throwable ex) {
                me.zhenxin.zmusic.dependencies.common.RuntimeLogger.logDependencyLoadFailure(url, startTime, ex);
                if (!ignoreException) {
                    throw ex;
                }
            }
        } else {
            loadDependencyLegacy(url, baseDir, relocation, repository, ignoreOptional, ignoreException, transitive, scope, external);
        }
    }

    void loadDependencyLegacy(
            @NotNull String url,
            @NotNull File baseDir,
            @NotNull List<JarRelocation> relocation,
            String repository,
            boolean ignoreOptional,
            boolean ignoreException,
            boolean transitive,
            @NotNull List<DependencyScope> scope,
            boolean external
    ) throws Throwable {
        Artifact artifact = new Artifact(url);
        DependencyDownloader downloader = new DependencyDownloader(baseDir, relocation);
        downloader.addRepository(new Repository(repository));
        downloader.setIgnoreOptional(ignoreOptional);
        downloader.setIgnoreException(ignoreException);
        downloader.setDependencyScopes(scope);
        downloader.setTransitive(transitive);
        // 解析依赖
        String pomPath = String.format(
                "%s/%s/%s/%s-%s.pom",
                artifact.getGroupId().replace('.', '/'),
                artifact.getArtifactId(),
                artifact.getVersion(),
                artifact.getArtifactId(),
                artifact.getVersion()
        );
        File pomFile = new File(baseDir, pomPath);
        File pomFile1 = new File(pomFile.getPath() + ".sha1");
        // 验证文件完整性
        if (PrimitiveIO.validation(pomFile, pomFile1)) {
            downloader.loadDependencyFromInputStream(pomFile.toPath().toUri().toURL().openStream());
        } else {
            downloader.loadDependencyFromInputStream(new URL(repository + "/" + pomPath).openStream());
        }
        // 加载自身
        Dependency dep = new Dependency(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(), DependencyScope.RUNTIME);
        dep.setType(artifact.getExtension());
        dep.setExternal(external);
        if (transitive) {
            downloader.injectClasspath(downloader.loadDependency(downloader.getRepositories(), dep));
        } else {
            downloader.injectClasspath(Collections.singleton(dep));
        }
    }

    /**
     * 顺序注入文件
     */
    private void injectFilesSequential(List<File> files, List<JarRelocation> relocation, boolean external, boolean ignoreException) throws Throwable {
        for (File file : files) {
            try {
                AetherResolver.inject(file, relocation, external);
            } catch (Throwable ex) {
                if (!ignoreException) {
                    me.zhenxin.zmusic.dependencies.common.RuntimeLogger.warning(t("✗ 注入依赖失败: {0} - {1}", "✗ Failed to inject dependency: {0} - {1}"), file.getName(), ex.getMessage());
                    throw ex;
                } else {
                    me.zhenxin.zmusic.dependencies.common.RuntimeLogger.debug(t("注入依赖失败(已忽略): {0} - {1}", "Failed to inject dependency (ignored): {0} - {1}"), file.getName(), ex.getMessage());
                }
            }
        }
    }

    /**
     * 并行注入文件（仅用于文件预处理，ClassLoader注入仍需要串行）
     */
    private void injectFilesParallel(List<File> files, List<JarRelocation> relocation, boolean external, boolean ignoreException) throws Throwable {
        // 注意：由于ClassLoader操作需要线程安全，这里只是优化重定向文件的生成过程
        // 实际的ClassLoader注入仍然是串行的，以确保安全性

        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(
                Math.min(DependencyConfig.MAX_PARALLEL_DOWNLOADS, files.size())
        );

        try {
            // 预处理阶段：并行准备重定向文件
            List<java.util.concurrent.Future<File>> futures = new ArrayList<>();
            for (File file : files) {
                futures.add(executor.submit(() -> {
                    try {
                        // 如果需要重定向，预先生成重定向文件
                        if (relocation != null && !relocation.isEmpty()) {
                            me.zhenxin.zmusic.dependencies.JarCacheManager cacheManager = me.zhenxin.zmusic.dependencies.JarCacheManager.getInstance();
                            File relocatedFile = cacheManager.getRelocatedFile(file, relocation.hashCode());
                            if (cacheManager.needsRelocate(file, relocatedFile)) {
                                // 在这里执行重定向操作，但不注入ClassLoader
                                me.zhenxin.zmusic.dependencies.common.RuntimeLogger.debug(t("预处理重定向文件: {0}", "Pre-processing relocation for: {0}"), file.getName());
                            }
                        }
                        return file;
                    } catch (Exception e) {
                        me.zhenxin.zmusic.dependencies.common.RuntimeLogger.debug(t("预处理文件失败: {0} - {1}", "Failed to pre-process file: {0} - {1}"), file.getName(), e.getMessage());
                        return file; // 返回原始文件，让后续串行处理
                    }
                }));
            }

            // 等待预处理完成
            for (java.util.concurrent.Future<File> future : futures) {
                try {
                    future.get(30, java.util.concurrent.TimeUnit.SECONDS); // 30秒超时
                } catch (java.util.concurrent.TimeoutException e) {
                    me.zhenxin.zmusic.dependencies.common.RuntimeLogger.debug(t("预处理超时，回退到顺序模式", "Pre-processing timeout, falling back to sequential"));
                    break;
                }
            }

        } finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        // 最终的ClassLoader注入必须串行进行，确保线程安全
        injectFilesSequential(files, relocation, external, ignoreException);
    }

    boolean test(String path) {
        String test = path.startsWith("!") ? path.substring(1) : path;
        return !test.isEmpty() && ClassAppender.isExists(test);
    }

}
