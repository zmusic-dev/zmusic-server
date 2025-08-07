package me.zhenxin.zmusic.dependencies.aether;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.lucko.jarrelocator.JarRelocator;
import me.lucko.jarrelocator.Relocation;
import me.zhenxin.zmusic.dependencies.DependencyScope;
import me.zhenxin.zmusic.dependencies.JarRelocation;
import me.zhenxin.zmusic.dependencies.common.ClassAppender;
import me.zhenxin.zmusic.dependencies.common.PrimitiveIO;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static me.zhenxin.zmusic.dependencies.common.PrimitiveIO.t;

/**
 * @author md_5, sky
 * @since 2024/7/20 20:31
 */
@SuppressWarnings("deprecation")

public class AetherResolver {

    private static final Map<String, AetherResolver> RESOLVER_MAP = Maps.newConcurrentMap();
    private static final Set<String> INJECTED_DEPENDENCIES = Sets.newConcurrentHashSet();

    private final RepositorySystem repository;
    private final DefaultRepositorySystemSession session;
    private final List<RemoteRepository> repositories;

    public AetherResolver(String repo) {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);
        this.repository = locator.getService(RepositorySystem.class);
        this.session = MavenRepositorySystemUtils.newSession();
        this.session.setChecksumPolicy("fail");
        this.session.setLocalRepositoryManager(this.repository.newLocalRepositoryManager(this.session, new LocalRepository("libraries")));
        this.session.setReadOnly();
        this.repositories = this.repository.newResolutionRepositories(this.session, Collections.singletonList(
                new RemoteRepository.Builder("central", "default", repo).build()
        ));
    }

    public static AetherResolver of(@NotNull String repository) {
        return RESOLVER_MAP.computeIfAbsent(repository, AetherResolver::new);
    }

    @SuppressWarnings("DuplicatedCode")
    public static void inject(@NotNull File file, @Nullable List<JarRelocation> relocation, boolean isExternal) throws Throwable {
        // 避免重复加载多个依赖
        String filePath = file.getPath();
        if (INJECTED_DEPENDENCIES.contains(filePath)) {
            me.zhenxin.zmusic.dependencies.common.RuntimeLogger.debug(t("跳过已加载的依赖: {0}", "Skipping already loaded dependency: {0}"), file.getName());
            return;
        }
        INJECTED_DEPENDENCIES.add(filePath);
        me.zhenxin.zmusic.dependencies.common.RuntimeLogger.debug(t("正在注入依赖文件: {0} ({1} bytes)", "Injecting dependency file: {0} ({1} bytes)"), file.getName(), file.length());
        // 如果没有重定向规则，直接注入
        if (relocation == null || relocation.isEmpty()) {
            ClassAppender.addPath(file.toPath(), isExternal);
            me.zhenxin.zmusic.dependencies.common.RuntimeLogger.debug(t("直接注入: {0}", "Direct injection: {0}"), file.getName());
        } else {
            // 获取重定向后的文件
            int relocationHash = relocation.hashCode();
            me.zhenxin.zmusic.dependencies.JarCacheManager cacheManager = me.zhenxin.zmusic.dependencies.JarCacheManager.getInstance();
            File rel = cacheManager.getRelocatedFile(file, relocationHash);

            // 检查重定向文件是否需要重新生成
            if (cacheManager.needsRelocate(file, rel)) {
                try {
                    // 获取重定向规则
                    List<Relocation> rules = relocation.stream().map(JarRelocation::toRelocation).collect(Collectors.toList());
                    // 获取临时文件
                    File tempSourceFile = PrimitiveIO.copyFile(file, File.createTempFile(file.getName(), ".jar"));
                    // 运行
                    new JarRelocator(tempSourceFile, rel, rules).run();
                } catch (IOException e) {
                    throw new IllegalStateException(String.format("Unable to relocate %s%n", file), e);
                }
            }
            // 注入重定向后的文件
            ClassAppender.addPath(rel.toPath(), isExternal);
            me.zhenxin.zmusic.dependencies.common.RuntimeLogger.debug(t("注入重定向文件: {0} -> {1}", "Injecting relocated file: {0} -> {1}"), file.getName(), rel.getName());
        }
    }

    public List<File> resolve(@NotNull String library, List<DependencyScope> scope, boolean isTransitive, boolean ignoreOptional) throws DependencyResolutionException {
        me.zhenxin.zmusic.dependencies.common.RuntimeLogger.debug(t("解析依赖: {0} (传递性: {1})", "Resolving dependency: {0} (transitive: {1})"), library, isTransitive);
        return resolveWithRetry(library, scope, isTransitive, ignoreOptional, me.zhenxin.zmusic.dependencies.DependencyConfig.DEFAULT_RETRY_COUNT);
    }

    private List<File> resolveWithRetry(@NotNull String library, List<DependencyScope> scope, boolean isTransitive, boolean ignoreOptional, int maxRetries) throws DependencyResolutionException {
        Dependency dependency = new Dependency(new DefaultArtifact(library), null);
        DependencyRequest dependencyRequest = getDependencyRequest(dependency, scope, isTransitive, ignoreOptional);

        DependencyResolutionException lastException = null;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                DependencyResult result = this.repository.resolveDependencies(this.session, dependencyRequest);
                List<File> files = result.getArtifactResults().stream().map(it -> it.getArtifact().getFile()).collect(Collectors.toList());
                me.zhenxin.zmusic.dependencies.common.RuntimeLogger.debug(t("解析完成: {0}, 获得 {1} 个文件", "Resolution completed: {0}, got {1} files"), library, files.size());
                return files;
            } catch (DependencyResolutionException e) {
                lastException = e;
                if (attempt < maxRetries) {
                    // 等待一段时间后重试，使用指数退避策略
                    try {
                        Thread.sleep(1000L * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new DependencyResolutionException(new DependencyResult(dependencyRequest), "Interrupted during retry", ie);
                    }
                }
            }
        }
        throw lastException;
    }

    private @NotNull DependencyRequest getDependencyRequest(Dependency dependency, List<DependencyScope> scope, boolean isTransitive, boolean ignoreOptional) {
        return new DependencyRequest(new CollectRequest(dependency, null, repositories), new DependencyFilter() {
            boolean self = true;

            @Override
            public boolean accept(DependencyNode node, List<DependencyNode> parents) {
                // 忽略可选
                if (ignoreOptional && node.getDependency().isOptional()) {
                    return false;
                }
                // 依赖传递
                if (isTransitive) {
                    return true;
                }
                if (self) {
                    self = false;
                    return true;
                }
                return false;
            }
        });
    }
}
