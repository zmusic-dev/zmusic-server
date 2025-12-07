package me.zhenxin.zmusic.utils.mod;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import me.zhenxin.zmusic.ZMusic;
import me.zhenxin.zmusic.ZMusicVelocity;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class SendVelocity implements Send {

    @Override
    public void sendAM(Object playerObj, String data) {
        if (!(playerObj instanceof Player)) {
            return;
        }
        Player player = (Player) playerObj;
        try {
            byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
            ByteBuf buf = Unpooled.buffer(bytes.length + 1);
            buf.writeByte(666);
            buf.writeBytes(bytes);
            byte[] finalData = buf.array();
            ZMusic.runTask.runAsync(() -> {
                player.sendPluginMessage(ZMusicVelocity.ALLMUSIC_CHANNEL, finalData);
                player.sendPluginMessage(ZMusicVelocity.ZMUSIC_CHANNEL, finalData);
            });
        } catch (Exception e) {
            ZMusic.log.sendDebugMessage("[Mod通信] 数据发送发生错误");
        }
    }

    @Override
    public void sendABF(Object playerObj, String data) {
        // Velocity 不支持 AudioBuffer
    }

    @Override
    public void sendToZMusicAddon(Object playerObj, String data) {
        if (!(playerObj instanceof Player)) {
            return;
        }
        Player player = (Player) playerObj;
        try {
            byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
            ByteBuf buf = Unpooled.buffer(bytes.length + 1);
            buf.writeByte(666);
            buf.writeBytes(bytes);
            byte[] finalData = buf.array();
            ZMusic.runTask.runAsync(() -> {
                Optional<ServerConnection> serverConnection = player.getCurrentServer();
                serverConnection.ifPresent(conn ->
                        conn.sendPluginMessage(ZMusicVelocity.ZMUSIC_CHANNEL, finalData));
            });
        } catch (Exception e) {
            ZMusic.log.sendDebugMessage("[Mod通信] 数据发送发生错误");
        }
    }
}
