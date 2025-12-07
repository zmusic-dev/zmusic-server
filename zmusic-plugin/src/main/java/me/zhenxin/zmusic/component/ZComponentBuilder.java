package me.zhenxin.zmusic.component;

/**
 * 组件构建器
 * <p>
 * 提供链式 API 构建复杂的消息组件
 */
public class ZComponentBuilder {

    private final ZTextComponent root;
    private ZTextComponent current;

    public ZComponentBuilder() {
        this.root = ZTextComponent.empty();
        this.current = root;
    }

    public ZComponentBuilder(String text) {
        this.root = ZTextComponent.of(text);
        this.current = root;
    }

    /**
     * 追加文本，创建新的子组件
     */
    public ZComponentBuilder append(String text) {
        ZTextComponent component = ZTextComponent.of(text);
        root.addChild(component);
        current = component;
        return this;
    }

    /**
     * 追加组件
     */
    public ZComponentBuilder append(ZComponent component) {
        root.addChild(component);
        if (component instanceof ZTextComponent) {
            current = (ZTextComponent) component;
        }
        return this;
    }

    /**
     * 为当前组件设置点击事件（执行命令）
     */
    public ZComponentBuilder clickRunCommand(String command) {
        current.setClickEvent(ZClickEvent.runCommand(command));
        return this;
    }

    /**
     * 为当前组件设置点击事件（建议命令）
     */
    public ZComponentBuilder clickSuggestCommand(String command) {
        current.setClickEvent(ZClickEvent.suggestCommand(command));
        return this;
    }

    /**
     * 为当前组件设置点击事件（打开 URL）
     */
    public ZComponentBuilder clickOpenUrl(String url) {
        current.setClickEvent(ZClickEvent.openUrl(url));
        return this;
    }

    /**
     * 为当前组件设置点击事件
     */
    public ZComponentBuilder click(ZClickEvent event) {
        current.setClickEvent(event);
        return this;
    }

    /**
     * 为当前组件设置悬停事件（显示文本）
     */
    public ZComponentBuilder hoverShowText(String text) {
        current.setHoverEvent(ZHoverEvent.showText(text));
        return this;
    }

    /**
     * 为当前组件设置悬停事件
     */
    public ZComponentBuilder hover(ZHoverEvent event) {
        current.setHoverEvent(event);
        return this;
    }

    /**
     * 构建最终组件
     */
    public ZComponent build() {
        return root;
    }

    /**
     * 静态工厂方法：创建空构建器
     */
    public static ZComponentBuilder create() {
        return new ZComponentBuilder();
    }

    /**
     * 静态工厂方法：使用初始文本创建构建器
     */
    public static ZComponentBuilder create(String text) {
        return new ZComponentBuilder(text);
    }
}
