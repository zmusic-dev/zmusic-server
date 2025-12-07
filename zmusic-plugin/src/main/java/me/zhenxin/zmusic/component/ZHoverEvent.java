package me.zhenxin.zmusic.component;

/**
 * 平台无关的悬停事件
 * <p>
 * 用于替代 BungeeCord 的 HoverEvent
 */
public class ZHoverEvent {

    private final Action action;
    private final String value;

    public ZHoverEvent(Action action, String value) {
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
     * 悬停事件类型
     */
    public enum Action {
        /**
         * 显示文本
         */
        SHOW_TEXT,
        /**
         * 显示物品
         */
        SHOW_ITEM,
        /**
         * 显示实体
         */
        SHOW_ENTITY
    }

    /**
     * 创建显示文本的悬停事件
     */
    public static ZHoverEvent showText(String text) {
        return new ZHoverEvent(Action.SHOW_TEXT, text);
    }
}
