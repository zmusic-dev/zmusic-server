package me.zhenxin.zmusic.utils.mod;

import com.velocitypowered.api.proxy.Player;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import me.zhenxin.zmusic.ZMusic;

import java.nio.charset.StandardCharsets;

public class SendVelocity implements Send {

    @Override
    public void sendAM(Object playerObj, String data) {
        Player player = (Player) playerObj;
        if (player == null)
            return;
        try {
            byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
            ByteBuf buf = Unpooled.buffer(bytes.length + 1);
            buf.writeByte(666);
            buf.writeBytes(bytes);
            byte[] finalData = buf.array();
            
            ZMusic.runTask.runAsync(() -> {
                player.sendPluginMessage("allmusic:channel", finalData);
                player.sendPluginMessage("zmusic:channel", finalData);
            });
        } catch (Exception e) {
            ZMusic.log.sendDebugMessage("[Mod通信] 数据发送发生错误");
        }
    }

    @Override
    public void sendABF(Object playerObj, String data) {
        Player player = (Player) playerObj;
        if (player == null)
            return;
        try {
            ZMusic.runTask.runAsync(() -> player.sendPluginMessage("AudioBuffer", data.getBytes()));
        } catch (Exception e) {
            ZMusic.log.sendDebugMessage("[Mod通信] 数据发送发生错误");
        }
    }
}