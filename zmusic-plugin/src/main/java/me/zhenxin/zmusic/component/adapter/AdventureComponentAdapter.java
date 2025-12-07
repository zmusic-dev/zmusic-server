package me.zhenxin.zmusic.component.adapter;

import me.zhenxin.zmusic.component.ZClickEvent;
import me.zhenxin.zmusic.component.ZComponent;
import me.zhenxin.zmusic.component.ZHoverEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * Adventure API 组件适配器
 * <p>
 * 将 ZComponent 转换为 Kyori Adventure 的 Component
 * 用于 Velocity 和 Paper 服务端
 */
public class AdventureComponentAdapter implements ComponentAdapter<Component> {

    public static final AdventureComponentAdapter INSTANCE = new AdventureComponentAdapter();

    private static final LegacyComponentSerializer LEGACY_SERIALIZER =
            LegacyComponentSerializer.legacySection();

    private AdventureComponentAdapter() {
    }

    @Override
    public Component adapt(ZComponent component) {
        // 使用 legacy serializer 解析带颜色代码的文本
        TextComponent.Builder builder = Component.text()
                .append(LEGACY_SERIALIZER.deserialize(component.getText()));

        // 适配点击事件
        if (component.getClickEvent() != null) {
            builder.clickEvent(adaptClickEvent(component.getClickEvent()));
        }

        // 适配悬停事件
        if (component.getHoverEvent() != null) {
            builder.hoverEvent(adaptHoverEvent(component.getHoverEvent()));
        }

        // 适配子组件
        for (ZComponent child : component.getChildren()) {
            builder.append(adapt(child));
        }

        return builder.build();
    }

    @Override
    public Component fromText(String text) {
        return LEGACY_SERIALIZER.deserialize(text);
    }

    private ClickEvent adaptClickEvent(ZClickEvent event) {
        switch (event.getAction()) {
            case OPEN_URL:
                return ClickEvent.openUrl(event.getValue());
            case RUN_COMMAND:
                return ClickEvent.runCommand(event.getValue());
            case SUGGEST_COMMAND:
                return ClickEvent.suggestCommand(event.getValue());
            case COPY_TO_CLIPBOARD:
                return ClickEvent.copyToClipboard(event.getValue());
            default:
                throw new IllegalArgumentException("Unknown click action: " + event.getAction());
        }
    }

    private HoverEvent<Component> adaptHoverEvent(ZHoverEvent event) {
        switch (event.getAction()) {
            case SHOW_TEXT:
                return HoverEvent.showText(LEGACY_SERIALIZER.deserialize(event.getValue()));
            case SHOW_ITEM:
            case SHOW_ENTITY:
                // 暂不支持复杂的悬停事件，回退到显示文本
                return HoverEvent.showText(Component.text(event.getValue()));
            default:
                throw new IllegalArgumentException("Unknown hover action: " + event.getAction());
        }
    }
}
