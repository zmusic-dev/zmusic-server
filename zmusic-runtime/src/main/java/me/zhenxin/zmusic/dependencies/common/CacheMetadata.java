package me.zhenxin.zmusic.dependencies.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 缓存元数据类
 * 存储依赖文件的哈希值、大小等信息，用于智能缓存判断
 *
 * @author ZMusic Team
 * @since 4.0.0
 */
public class CacheMetadata {

    private final String sha256;
    private final long fileSize;
    private final long lastModified;
    private final long createdTime;
    private final int relocationHash;

    /**
     * 创建缓存元数据
     *
     * @param sha256         文件的 SHA-256 哈希值
     * @param fileSize       文件大小（字节）
     * @param lastModified   上次修改时间（毫秒时间戳）
     * @param createdTime    创建时间（毫秒时间戳）
     * @param relocationHash 重定位规则的哈希值
     */
    public CacheMetadata(@NotNull String sha256, long fileSize, long lastModified, long createdTime, int relocationHash) {
        this.sha256 = sha256;
        this.fileSize = fileSize;
        this.lastModified = lastModified;
        this.createdTime = createdTime;
        this.relocationHash = relocationHash;
    }

    /**
     * 从文件创建元数据
     *
     * @param file           源文件
     * @param relocationHash 重定位规则的哈希值
     * @return 元数据对象
     * @throws IOException 如果读取文件失败
     */
    @NotNull
    public static CacheMetadata fromFile(@NotNull File file, int relocationHash) throws IOException {
        String sha256 = FileHashUtil.sha256(file);
        long fileSize = file.length();
        long lastModified = file.lastModified();
        long createdTime = System.currentTimeMillis();

        return new CacheMetadata(sha256, fileSize, lastModified, createdTime, relocationHash);
    }

    /**
     * 从 JSON 字符串解析元数据（简化版，不依赖 JSON 库）
     *
     * @param json JSON 字符串
     * @return 元数据对象，解析失败返回 null
     */
    @Nullable
    public static CacheMetadata fromJson(@NotNull String json) {
        try {
            Map<String, String> map = parseSimpleJson(json);
            String sha256 = map.get("sha256");
            long fileSize = Long.parseLong(map.get("fileSize"));
            long lastModified = Long.parseLong(map.get("lastModified"));
            long createdTime = Long.parseLong(map.get("createdTime"));
            int relocationHash = Integer.parseInt(map.get("relocationHash"));

            return new CacheMetadata(sha256, fileSize, lastModified, createdTime, relocationHash);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从文件加载元数据
     *
     * @param metadataFile 元数据文件
     * @return 元数据对象，文件不存在或解析失败返回 null
     */
    @Nullable
    public static CacheMetadata loadFromFile(@NotNull File metadataFile) {
        if (!metadataFile.exists() || !metadataFile.isFile()) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(metadataFile))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return fromJson(sb.toString());
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 简单的 JSON 解析器（仅支持单层键值对）
     */
    private static Map<String, String> parseSimpleJson(String json) {
        Map<String, String> map = new HashMap<>();
        // 去掉外层的 {}
        String content = json.trim();
        if (content.startsWith("{")) {
            content = content.substring(1);
        }
        if (content.endsWith("}")) {
            content = content.substring(0, content.length() - 1);
        }

        // 分割键值对
        String[] pairs = content.split(",");
        for (String pair : pairs) {
            String[] kv = pair.split(":", 2);
            if (kv.length == 2) {
                String key = kv[0].trim().replace("\"", "");
                String value = kv[1].trim().replace("\"", "");
                map.put(key, value);
            }
        }

        return map;
    }

    /**
     * 转换为 JSON 字符串
     */
    @NotNull
    public String toJson() {
        return String.format(
                "{\"sha256\":\"%s\",\"fileSize\":%d,\"lastModified\":%d,\"createdTime\":%d,\"relocationHash\":%d}",
                sha256, fileSize, lastModified, createdTime, relocationHash
        );
    }

    /**
     * 保存元数据到文件
     *
     * @param metadataFile 元数据文件
     * @throws IOException 如果写入文件失败
     */
    public void saveToFile(@NotNull File metadataFile) throws IOException {
        // 确保父目录存在
        File parent = metadataFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(metadataFile))) {
            writer.write(toJson());
        }
    }

    /**
     * 验证文件是否与元数据匹配
     *
     * @param file           文件
     * @param relocationHash 重定位规则的哈希值
     * @return 是否匹配
     */
    public boolean matches(@NotNull File file, int relocationHash) {
        if (!file.exists() || !file.isFile()) {
            return false;
        }

        // 检查重定位规则是否一致
        if (this.relocationHash != relocationHash) {
            return false;
        }

        // 检查文件大小
        if (file.length() != this.fileSize) {
            return false;
        }

        // 检查修改时间
        if (file.lastModified() != this.lastModified) {
            return false;
        }

        // 如果大小和修改时间都匹配，验证 SHA-256
        try {
            String actualSha256 = FileHashUtil.sha256(file);
            return this.sha256.equalsIgnoreCase(actualSha256);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 检查缓存是否过期
     *
     * @param maxAgeMs 最大缓存时间（毫秒）
     * @return 是否过期
     */
    public boolean isExpired(long maxAgeMs) {
        return System.currentTimeMillis() - createdTime > maxAgeMs;
    }

    // Getters

    @NotNull
    public String getSha256() {
        return sha256;
    }

    public long getFileSize() {
        return fileSize;
    }

    public long getLastModified() {
        return lastModified;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public int getRelocationHash() {
        return relocationHash;
    }

    @Override
    public String toString() {
        return "CacheMetadata{" +
                "sha256='" + sha256 + '\'' +
                ", fileSize=" + fileSize +
                ", lastModified=" + lastModified +
                ", createdTime=" + createdTime +
                ", relocationHash=" + relocationHash +
                '}';
    }
}
