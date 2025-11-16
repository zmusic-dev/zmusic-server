package me.zhenxin.zmusic.dependencies.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 文件哈希计算工具类
 * 提供 SHA-256 等哈希算法
 *
 * @author ZMusic Team
 * @since 4.0.0
 */
public class FileHashUtil {

    private static final int BUFFER_SIZE = 8192;

    /**
     * 计算文件的 SHA-256 哈希值
     *
     * @param file 文件
     * @return SHA-256 哈希值（十六进制字符串）
     * @throws IOException 如果读取文件失败
     */
    public static String sha256(File file) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[BUFFER_SIZE];
                int read;
                while ((read = fis.read(buffer)) != -1) {
                    digest.update(buffer, 0, read);
                }
            }
            return bytesToHex(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * 计算文件的 MD5 哈希值（用于快速校验）
     *
     * @param file 文件
     * @return MD5 哈希值（十六进制字符串）
     * @throws IOException 如果读取文件失败
     */
    public static String md5(File file) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[BUFFER_SIZE];
                int read;
                while ((read = fis.read(buffer)) != -1) {
                    digest.update(buffer, 0, read);
                }
            }
            return bytesToHex(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not available", e);
        }
    }

    /**
     * 验证文件哈希值
     *
     * @param file         文件
     * @param expectedHash 期望的哈希值
     * @param algorithm    算法（"SHA-256" 或 "MD5"）
     * @return 是否匹配
     * @throws IOException 如果读取文件失败
     */
    public static boolean verify(File file, String expectedHash, String algorithm) throws IOException {
        String actualHash;
        if ("SHA-256".equalsIgnoreCase(algorithm)) {
            actualHash = sha256(file);
        } else if ("MD5".equalsIgnoreCase(algorithm)) {
            actualHash = md5(file);
        } else {
            throw new IllegalArgumentException("Unsupported algorithm: " + algorithm);
        }
        return actualHash.equalsIgnoreCase(expectedHash);
    }

    /**
     * 字节数组转十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
