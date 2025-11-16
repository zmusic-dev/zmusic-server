package me.zhenxin.zmusic.dependencies.exception;

/**
 * 依赖下载异常
 * 当依赖文件下载失败、校验失败或网络错误时抛出
 *
 * @author ZMusic Team
 * @since 4.0.0
 */
public class DependencyDownloadException extends DependencyException {

    private final String coordinate;
    private final String repository;

    /**
     * 创建依赖下载异常
     *
     * @param coordinate 依赖坐标
     * @param repository 仓库 URL
     * @param message    异常消息
     */
    public DependencyDownloadException(String coordinate, String repository, String message) {
        super("Failed to download dependency '" + coordinate + "' from '" + repository + "': " + message);
        this.coordinate = coordinate;
        this.repository = repository;
    }

    /**
     * 创建依赖下载异常（带原因）
     *
     * @param coordinate 依赖坐标
     * @param repository 仓库 URL
     * @param message    异常消息
     * @param cause      原始异常
     */
    public DependencyDownloadException(String coordinate, String repository, String message, Throwable cause) {
        super("Failed to download dependency '" + coordinate + "' from '" + repository + "': " + message, cause);
        this.coordinate = coordinate;
        this.repository = repository;
    }

    /**
     * 获取失败的依赖坐标
     */
    public String getCoordinate() {
        return coordinate;
    }

    /**
     * 获取失败的仓库 URL
     */
    public String getRepository() {
        return repository;
    }
}
