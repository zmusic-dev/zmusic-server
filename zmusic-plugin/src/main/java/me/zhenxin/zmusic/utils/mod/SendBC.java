package me.zhenxin.zmusic.utils.mod;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import me.zhenxin.zmusic.ZMusic;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.nio.charset.StandardCharsets;

public class SendBC implements Send {

    @Override
    public void sendAM(Object playerObj, String data) {
        ProxiedPlayer player = (ProxiedPlayer) playerObj;
        if (player == null)
            return;
        try {
            byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
            ByteBuf buf = Unpooled.buffer(bytes.length + 1);
            buf.writeByte(666);
            buf.writeBytes(bytes);
            byte[] array = buf.array();
            // 同步发送到两个频道，保证 [Stop] 和 [Play] 的顺序，
            // 避免因 runAsync 线程池竞争导致的播放无声问题
            player.sendData("allmusic:channel", array);
            player.sendData("zmusic:channel", array);
        } catch (
            Exception e) {
            ZMusic.log.sendDebugMessage("[Mod通信] 数据发送发生错误");
        }
    }

    @Override
    public void sendABF(Object playerObj, String data) {
        ProxiedPlayer player = (ProxiedPlayer) playerObj;
        if (player == null)
            return;
        try {
            ZMusic.runTask.runAsync(() -> player.sendData("AudioBuffer", data.getBytes()));
        } catch (
            Exception e) {
            ZMusic.log.sendDebugMessage("[Mod通信] 数据发送发生错误");
        }
    }

    @Override
    public void sendToZMusicAddon(Object playerObj, String data) {
        ProxiedPlayer player = (ProxiedPlayer) playerObj;
        if (player == null)
            return;
        try {
            byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
            ByteBuf buf = Unpooled.buffer(bytes.length + 1);
            buf.writeByte(666);
            buf.writeBytes(bytes);
            ZMusic.runTask.runAsync(() -> player.getServer().getInfo().sendData("zmusic:channel", buf.array()));
        } catch (
            Exception e) {
            ZMusic.log.sendDebugMessage("[Mod通信] 数据发送发生错误");
        }
    }
}
