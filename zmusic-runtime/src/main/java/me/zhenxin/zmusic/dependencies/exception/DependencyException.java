package me.zhenxin.zmusic.dependencies.exception;

/**
 * 依赖管理基础异常类
 * 所有依赖相关异常的父类
 *
 * @author ZMusic Team
 * @since 4.0.0
 */
public class DependencyException extends Exception {

    /**
     * 创建异常
     *
     * @param message 异常消息
     */
    public DependencyException(String message) {
        super(message);
    }

    /**
     * 创建异常（带原因）
     *
     * @param message 异常消息
     * @param cause   原始异常
     */
    public DependencyException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 创建异常（仅原因）
     *
     * @param cause 原始异常
     */
    public DependencyException(Throwable cause) {
        super(cause);
    }
}
