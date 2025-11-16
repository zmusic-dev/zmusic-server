package me.zhenxin.zmusic.dependencies.exception;

/**
 * 依赖解析异常
 * 当依赖坐标无效、版本冲突或 POM 解析失败时抛出
 *
 * @author ZMusic Team
 * @since 4.0.0
 */
public class DependencyResolutionException extends DependencyException {

    private final String coordinate;

    /**
     * 创建依赖解析异常
     *
     * @param coordinate 依赖坐标 (groupId:artifactId:version)
     * @param message    异常消息
     */
    public DependencyResolutionException(String coordinate, String message) {
        super("Failed to resolve dependency '" + coordinate + "': " + message);
        this.coordinate = coordinate;
    }

    /**
     * 创建依赖解析异常（带原因）
     *
     * @param coordinate 依赖坐标
     * @param message    异常消息
     * @param cause      原始异常
     */
    public DependencyResolutionException(String coordinate, String message, Throwable cause) {
        super("Failed to resolve dependency '" + coordinate + "': " + message, cause);
        this.coordinate = coordinate;
    }

    /**
     * 获取失败的依赖坐标
     */
    public String getCoordinate() {
        return coordinate;
    }
}
