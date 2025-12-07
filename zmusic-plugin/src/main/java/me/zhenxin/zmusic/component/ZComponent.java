package me.zhenxin.zmusic.component;

import java.util.List;

/**
 * 平台无关的消息组件接口
 * <p>
 * 用于替代 BungeeCord 的 TextComponent，支持 Bukkit/BungeeCord/Velocity 三平台
 */
public interface ZComponent {

    /**
     * 获取组件的纯文本内容
     */
    String getText();

    /**
     * 设置文本内容
     */
    ZComponent setText(String text);

    /**
     * 获取点击事件
     */
    ZClickEvent getClickEvent();

    /**
     * 设置点击事件
     */
    ZComponent setClickEvent(ZClickEvent clickEvent);

    /**
     * 获取悬停事件
     */
    ZHoverEvent getHoverEvent();

    /**
     * 设置悬停事件
     */
    ZComponent setHoverEvent(ZHoverEvent hoverEvent);

    /**
     * 获取子组件列表
     */
    List<ZComponent> getChildren();

    /**
     * 添加子组件
     */
    ZComponent addChild(ZComponent child);

    /**
     * 转换为带格式的完整文本（包含子组件）
     */
    String toPlainText();
}
