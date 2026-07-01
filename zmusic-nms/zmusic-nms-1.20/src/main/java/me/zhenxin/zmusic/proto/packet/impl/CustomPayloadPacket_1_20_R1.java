package me.zhenxin.zmusic.proto.packet.impl;

import io.netty.buffer.Unpooled;
import me.zhenxin.zmusic.proto.packet.CustomPayloadPacket;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.game.PacketPlayOutCustomPayload;
import net.minecraft.resources.MinecraftKey;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * @author 真心
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class CustomPayloadPacket_1_20_R1 extends CustomPayloadPacket {

    public CustomPayloadPacket_1_20_R1(Player player, String channel, byte[] data) {
        super(player, channel, data);
    }

    @Override
    public void send() {
        MinecraftKey key = new MinecraftKey(channel);
        PacketDataSerializer buf = new PacketDataSerializer(Unpooled.wrappedBuffer(data));
        PacketPlayOutCustomPayload packet = new PacketPlayOutCustomPayload(key, buf);
        CraftPlayer craftPlayer = (CraftPlayer) player;
        craftPlayer.getHandle().c.a(packet);
    }
}
