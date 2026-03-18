package me.zhenxin.zmusic.dependencies;

import me.lucko.jarrelocator.JarRelocator;
import me.lucko.jarrelocator.Relocation;
import me.zhenxin.zmusic.ZMusicConstants;
import me.zhenxin.zmusic.dependencies.common.BootstrapContext;
import me.zhenxin.zmusic.dependencies.common.ClassAppender;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Java bootstrap 阶段专用的 Kotlin 运行时加载器。
 * 这里只保留 stdlib 自举所需的最小能力，后续依赖编排交给 Kotlin。
 */
public final class KotlinBootstrapLoader {

    private static final int BUFFER_SIZE = 8192;
    private static final int KOTLIN_RELOCATION_HASH = 682069081;
    private static final String KOTLIN_GROUP = "!org.jetbrains.kotlin".substring(1);
    private static final String KOTLIN_STDLIB = "!kotlin-stdlib".substring(1);
    private static final String KOTLIN_STDLIB_JDK8 = "!kotlin-stdlib-jdk8".substring(1);
    private static final String KOTLIN_PACKAGE = "!kotlin.".substring(1);
    private static final String RELOCATED_KOTLIN_PACKAGE = "!me.zhenxin.zmusic.library.kotlin.".substring(1);
    private static final Relocation KOTLIN_RELOCATION = new Relocation(KOTLIN_PACKAGE, RELOCATED_KOTLIN_PACKAGE);
    private static final Set<String> INJECTED_FILES = Collections.synchronizedSet(new HashSet<>());

    private KotlinBootstrapLoader() {
    }

    public static void bootstrap(String dataFolder) throws Throwable {
        prepareRuntimeEnvironment(dataFolder);
        loadBootstrapArtifact(KOTLIN_GROUP, KOTLIN_STDLIB, ZMusicConstants.KOTLIN_VERSION);
        loadBootstrapArtifact(KOTLIN_GROUP, KOTLIN_STDLIB_JDK8, ZMusicConstants.KOTLIN_VERSION);
    }

    private static void prepareRuntimeEnvironment(String dataFolder) {
        File dataFolderFile = new File(dataFolder);
        if (!dataFolderFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            dataFolderFile.mkdirs();
        }

        File libraryDir = new File(BootstrapContext.getDefaultLibrary());
        if (!libraryDir.isAbsolute()) {
            BootstrapContext.setDefaultLibrary(new File(dataFolderFile, BootstrapContext.getDefaultLibrary()).getPath());
        }
    }

    private static void loadBootstrapArtifact(String groupId, String artifactId, String version) throws Throwable {
        File originalJar = ensureArtifactJar(groupId, artifactId, version);
        BootstrapContext.debug(BootstrapContext.t(
                "正在注入依赖文件: {0} ({1} bytes)",
                "Injecting dependency file: {0} ({1} bytes)"
        ), originalJar.getName(), originalJar.length());

        File relocatedJar = relocateIfNeeded(originalJar);
        if (INJECTED_FILES.add(relocatedJar.getPath())) {
            ClassAppender.addPath(relocatedJar.toPath(), true);
        }

        BootstrapContext.debug(BootstrapContext.t(
                "注入重定向文件: {0} -> {1}",
                "Injecting relocated file: {0} -> {1}"
        ), originalJar.getName(), relocatedJar.getName());
    }

    private static File ensureArtifactJar(String groupId, String artifactId, String version) throws IOException {
        File jarFile = buildArtifactFile(groupId, artifactId, version, "jar");
        File sha1File = new File(jarFile.getPath() + ".sha1");
        if (isValid(jarFile, sha1File)) {
            return jarFile;
        }

        String artifactBasePath = groupId.replace('.', '/') + "/" + artifactId + "/" + version + "/";
        downloadFile(artifactBasePath + artifactId + "-" + version + ".jar", jarFile.toPath());
        downloadFile(artifactBasePath + artifactId + "-" + version + ".jar.sha1", sha1File.toPath());

        if (!isValid(jarFile, sha1File)) {
            throw new IOException("Downloaded Kotlin artifact is invalid: " + jarFile.getName());
        }
        return jarFile;
    }

    private static File relocateIfNeeded(File originalJar) throws Exception {
        File relocatedJar = relocatedArtifactFile(originalJar);
        if (relocatedJar.exists() && relocatedJar.length() > 0 && relocatedJar.lastModified() >= originalJar.lastModified()) {
            return relocatedJar;
        }

        File tempSource = Files.createTempFile("zmusic-kotlin-bootstrap-", ".jar").toFile();
        try {
            Files.copy(originalJar.toPath(), tempSource.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            new JarRelocator(tempSource, relocatedJar, Collections.singletonList(KOTLIN_RELOCATION)).run();
            relocatedJar.setLastModified(originalJar.lastModified());
            return relocatedJar;
        } finally {
            //noinspection ResultOfMethodCallIgnored
            tempSource.delete();
        }
    }

    private static File buildArtifactFile(String groupId, String artifactId, String version, String extension) {
        String relativePath = groupId.replace('.', File.separatorChar)
                + File.separator + artifactId
                + File.separator + version
                + File.separator + artifactId + "-" + version + "." + extension;
        return new File(BootstrapContext.getDefaultLibrary(), relativePath);
    }

    private static File relocatedArtifactFile(File originalJar) {
        String name = originalJar.getName();
        int dotIndex = name.lastIndexOf('.');
        String baseName = dotIndex > 0 ? name.substring(0, dotIndex) : name;
        return new File(originalJar.getParentFile(), baseName + "_r2_" + KOTLIN_RELOCATION_HASH + ".jar");
    }

    private static void downloadFile(String relativePath, Path target) throws IOException {
        Files.createDirectories(target.getParent());
        URI uri = URI.create(BootstrapContext.getRepository() + "/" + relativePath);
        try (InputStream inputStream = uri.toURL().openStream();
             OutputStream outputStream = Files.newOutputStream(target)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
        }
    }

    private static boolean isValid(File file, File sha1File) throws IOException {
        return file.exists()
                && sha1File.exists()
                && readUtf8(sha1File.toPath()).startsWith(sha1(file));
    }

    private static String sha1(File file) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            try (InputStream inputStream = Files.newInputStream(file.toPath())) {
                byte[] buffer = new byte[BUFFER_SIZE];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    digest.update(buffer, 0, read);
                }
            }
            return bytesToHex(digest.digest());
        } catch (NoSuchAlgorithmException exception) {
            throw new IOException("SHA-1 algorithm is not available", exception);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            stringBuilder.append(String.format("%02x", b));
        }
        return stringBuilder.toString();
    }

    private static String readUtf8(Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }
}
