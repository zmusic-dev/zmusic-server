package me.zhenxin.zmusic.proto;

import me.zhenxin.zmusic.ZMusic;
import me.zhenxin.zmusic.ZMusicBukkit;
import me.zhenxin.zmusic.config.Config;
import me.zhenxin.zmusic.proto.packet.CustomPayloadPacket;
import me.zhenxin.zmusic.proto.packet.impl.*;
import org.bukkit.entity.Player;

/**
 * 通过 NMS 直接向客户端发送插件消息包，绕过 Arclight 等服务端实现
 * 在 CraftBukkit 层对插件消息通道注册状态的检查 (Messenger#getListeningPluginChannels)。
 *
 * @author 真心
 */
public class NmsCustomPayload {

    /**
     * 尝试通过 NMS 直接发送插件消息包。
     *
     * @return 当前 NMS 版本支持该方式并发送成功时返回 true，否则返回 false（调用方应回退到普通的插件消息发送）
     */
    public static boolean trySend(Object playerObj, String channel, byte[] data) {
        try {
            Player player = (Player) playerObj;
            if (player == null) {
                return false;
            }
            String packageName = ZMusicBukkit.plugin.getServer().getClass().getPackage().getName();
            String nms = packageName.substring(packageName.lastIndexOf('.') + 1);
            CustomPayloadPacket packet;
            switch (nms) {
                case "v1_20_R1":
                    packet = new CustomPayloadPacket_1_20_R1(player, channel, data);
                    break;
                case "v1_19_R3":
                    packet = new CustomPayloadPacket_1_19_R3(player, channel, data);
                    break;
                case "v1_19_R2":
                    packet = new CustomPayloadPacket_1_19_R2(player, channel, data);
                    break;
                case "v1_19_R1":
                    packet = new CustomPayloadPacket_1_19_R1(player, channel, data);
                    break;
                case "v1_18_R2":
                    packet = new CustomPayloadPacket_1_18_R2(player, channel, data);
                    break;
                case "v1_18_R1":
                    packet = new CustomPayloadPacket_1_18_R1(player, channel, data);
                    break;
                case "v1_17_R1":
                    packet = new CustomPayloadPacket_1_17_R1(player, channel, data);
                    break;
                case "v1_16_R3":
                    packet = new CustomPayloadPacket_1_16_R3(player, channel, data);
                    break;
                case "v1_16_R2":
                    packet = new CustomPayloadPacket_1_16_R2(player, channel, data);
                    break;
                case "v1_16_R1":
                    packet = new CustomPayloadPacket_1_16_R1(player, channel, data);
                    break;
                case "v1_15_R1":
                    packet = new CustomPayloadPacket_1_15_R1(player, channel, data);
                    break;
                case "v1_14_R1":
                    packet = new CustomPayloadPacket_1_14_R1(player, channel, data);
                    break;
                case "v1_13_R2":
                    packet = new CustomPayloadPacket_1_13_R2(player, channel, data);
                    break;
                case "v1_12_R1":
                    packet = new CustomPayloadPacket_1_12_R1(player, channel, data);
                    break;
                default:
                    return false;
            }
            packet.send();
            return true;
        } catch (Throwable t) {
            if (Config.debug) {
                t.printStackTrace();
            }
            ZMusic.log.sendDebugMessage("[Mod通信] NMS直发插件消息失败, 回退到标准方式");
            return false;
        }
    }
}
