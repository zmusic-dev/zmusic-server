package me.zhenxin.zmusic.utils.message;

import me.zhenxin.zmusic.component.ZComponent;
import me.zhenxin.zmusic.component.ZTextComponent;
import me.zhenxin.zmusic.language.Lang;

/**
 * 消息发送接口
 * <p>
 * 平台无关的消息发送抽象
 */
public interface Message {

    void sendNormalMessage(String message, Object playerObj);

    void sendErrorMessage(String message, Object playerObj);

    /**
     * 发送富文本消息（支持点击、悬停事件）
     */
    void sendJsonMessage(ZComponent message, Object playerObj);

    /**
     * 发送 ActionBar 消息
     */
    default void sendActionBarMessage(String message, Object playerObj) {
        sendActionBarMessage(ZTextComponent.of(message), playerObj);
    }

    /**
     * 发送 ActionBar 消息（富文本）
     */
    void sendActionBarMessage(ZComponent message, Object playerObj);

    void sendTitleMessage(String title, String subTitle, Object playerObj);

    void sendNull(Object playerObj);

    default void sendPlayError(Object playerObj, String musicName) {
        for (String s : Lang.playError) {
            sendErrorMessage(s.replaceAll("%musicName%", musicName), playerObj);
        }
    }
}
