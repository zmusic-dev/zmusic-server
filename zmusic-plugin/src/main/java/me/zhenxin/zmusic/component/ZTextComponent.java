package me.zhenxin.zmusic.component;

import java.util.ArrayList;
import java.util.List;

/**
 * ZComponent 的默认实现
 * <p>
 * 文本组件，支持点击事件、悬停事件和子组件
 */
public class ZTextComponent implements ZComponent {

    private String text;
    private ZClickEvent clickEvent;
    private ZHoverEvent hoverEvent;
    private final List<ZComponent> children;

    public ZTextComponent() {
        this("");
    }

    public ZTextComponent(String text) {
        this.text = text;
        this.children = new ArrayList<>();
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public ZComponent setText(String text) {
        this.text = text;
        return this;
    }

    @Override
    public ZClickEvent getClickEvent() {
        return clickEvent;
    }

    @Override
    public ZComponent setClickEvent(ZClickEvent clickEvent) {
        this.clickEvent = clickEvent;
        return this;
    }

    @Override
    public ZHoverEvent getHoverEvent() {
        return hoverEvent;
    }

    @Override
    public ZComponent setHoverEvent(ZHoverEvent hoverEvent) {
        this.hoverEvent = hoverEvent;
        return this;
    }

    @Override
    public List<ZComponent> getChildren() {
        return children;
    }

    @Override
    public ZComponent addChild(ZComponent child) {
        children.add(child);
        return this;
    }

    @Override
    public String toPlainText() {
        StringBuilder sb = new StringBuilder(text);
        for (ZComponent child : children) {
            sb.append(child.toPlainText());
        }
        return sb.toString();
    }

    /**
     * 创建一个新的文本组件
     */
    public static ZTextComponent of(String text) {
        return new ZTextComponent(text);
    }

    /**
     * 创建一个空的文本组件
     */
    public static ZTextComponent empty() {
        return new ZTextComponent();
    }
}
