package me.zhenxin.zmusic.component.adapter;

import me.zhenxin.zmusic.component.ZComponent;

/**
 * 组件适配器接口
 * <p>
 * 将 ZComponent 转换为平台特定的组件类型
 *
 * @param <T> 平台特定的组件类型
 */
public interface ComponentAdapter<T> {

    /**
     * 将 ZComponent 转换为平台特定的组件
     *
     * @param component ZComponent 实例
     * @return 平台特定的组件
     */
    T adapt(ZComponent component);

    /**
     * 从纯文本创建平台特定的组件
     *
     * @param text 纯文本
     * @return 平台特定的组件
     */
    T fromText(String text);
}
