package me.zhenxin.zmusic.component.adapter;

import me.zhenxin.zmusic.component.ZClickEvent;
import me.zhenxin.zmusic.component.ZComponent;
import me.zhenxin.zmusic.component.ZHoverEvent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * BungeeCord 组件适配器
 * <p>
 * 将 ZComponent 转换为 BungeeCord 的 TextComponent
 */
public class BungeeComponentAdapter implements ComponentAdapter<TextComponent> {

    public static final BungeeComponentAdapter INSTANCE = new BungeeComponentAdapter();

    private BungeeComponentAdapter() {
    }

    @Override
    public TextComponent adapt(ZComponent component) {
        TextComponent textComponent = new TextComponent(component.getText());

        // 适配点击事件
        if (component.getClickEvent() != null) {
            textComponent.setClickEvent(adaptClickEvent(component.getClickEvent()));
        }

        // 适配悬停事件
        if (component.getHoverEvent() != null) {
            textComponent.setHoverEvent(adaptHoverEvent(component.getHoverEvent()));
        }

        // 适配子组件
        for (ZComponent child : component.getChildren()) {
            textComponent.addExtra(adapt(child));
        }

        return textComponent;
    }

    @Override
    public TextComponent fromText(String text) {
        return new TextComponent(text);
    }

    private ClickEvent adaptClickEvent(ZClickEvent event) {
        ClickEvent.Action action;
        switch (event.getAction()) {
            case OPEN_URL:
                action = ClickEvent.Action.OPEN_URL;
                break;
            case RUN_COMMAND:
                action = ClickEvent.Action.RUN_COMMAND;
                break;
            case SUGGEST_COMMAND:
                action = ClickEvent.Action.SUGGEST_COMMAND;
                break;
            case COPY_TO_CLIPBOARD:
                action = ClickEvent.Action.COPY_TO_CLIPBOARD;
                break;
            default:
                throw new IllegalArgumentException("Unknown click action: " + event.getAction());
        }
        return new ClickEvent(action, event.getValue());
    }

    @SuppressWarnings("deprecation")
    private HoverEvent adaptHoverEvent(ZHoverEvent event) {
        HoverEvent.Action action;
        switch (event.getAction()) {
            case SHOW_TEXT:
                action = HoverEvent.Action.SHOW_TEXT;
                break;
            case SHOW_ITEM:
                action = HoverEvent.Action.SHOW_ITEM;
                break;
            case SHOW_ENTITY:
                action = HoverEvent.Action.SHOW_ENTITY;
                break;
            default:
                throw new IllegalArgumentException("Unknown hover action: " + event.getAction());
        }
        // 使用旧版 API 以保持兼容性
        return new HoverEvent(action, new ComponentBuilder(event.getValue()).create());
    }
}
