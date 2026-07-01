package me.zhenxin.zmusic.proto.packet;

import org.bukkit.entity.Player;

/**
 * 直接通过 NMS 向客户端发送自定义插件消息包，绕过部分服务端实现（如 Arclight）
 * 在 CraftBukkit 层对插件消息通道的注册检查。
 *
 * @author 真心
 */
public abstract class CustomPayloadPacket {

    protected final Player player;
    protected final String channel;
    protected final byte[] data;

    public CustomPayloadPacket(Player player, String channel, byte[] data) {
        this.player = player;
        this.channel = channel;
        this.data = data;
    }

    public abstract void send();
}
