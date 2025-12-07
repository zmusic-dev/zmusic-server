package me.zhenxin.zmusic.component;

/**
 * 平台无关的点击事件
 * <p>
 * 用于替代 BungeeCord 的 ClickEvent
 */
public class ZClickEvent {

    private final Action action;
    private final String value;

    public ZClickEvent(Action action, String value) {
        this.action = action;
        this.value = value;
    }

    public Action getAction() {
        return action;
    }

    public String getValue() {
        return value;
    }

    /**
     * 点击事件类型
     */
    public enum Action {
        /**
         * 打开 URL
         */
        OPEN_URL,
        /**
         * 执行命令
         */
        RUN_COMMAND,
        /**
         * 建议命令（填充到聊天框）
         */
        SUGGEST_COMMAND,
        /**
         * 复制到剪贴板
         */
        COPY_TO_CLIPBOARD
    }

    /**
     * 创建执行命令的点击事件
     */
    public static ZClickEvent runCommand(String command) {
        return new ZClickEvent(Action.RUN_COMMAND, command);
    }

    /**
     * 创建建议命令的点击事件
     */
    public static ZClickEvent suggestCommand(String command) {
        return new ZClickEvent(Action.SUGGEST_COMMAND, command);
    }

    /**
     * 创建打开 URL 的点击事件
     */
    public static ZClickEvent openUrl(String url) {
        return new ZClickEvent(Action.OPEN_URL, url);
    }

    /**
     * 创建复制到剪贴板的点击事件
     */
    public static ZClickEvent copyToClipboard(String text) {
        return new ZClickEvent(Action.COPY_TO_CLIPBOARD, text);
    }
}
