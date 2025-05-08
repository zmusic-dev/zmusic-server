package me.zhenxin.zmusic.proto.packet.impl;

import me.zhenxin.zmusic.proto.packet.AdvancementPacket;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.advancements.*;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Optional;

/**
 * @author Lumine1909
 * @since 2025/5/8 10:22
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class AdvancementPacket_Paper extends AdvancementPacket {

    private static final AdvancementRequirements SIMPLE_REQUIREMENT = new AdvancementRequirements(Collections.singletonList(Collections.singletonList("1")));

    private final ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(namespaced, key);
    private final Component messageComponent;
    private final Component descriptionComponent;

    public AdvancementPacket_Paper(Player player, String message) {
        super(player, message);
        RegistryAccess.Frozen frozen = MinecraftServer.getServer().registryAccess();
        Component component1 = Component.Serializer.fromJson(GsonComponentSerializer.gson().serialize(LegacyComponentSerializer.legacyAmpersand().deserialize(message)), frozen);
        Component component2 = Component.Serializer.fromJson(GsonComponentSerializer.gson().serialize(LegacyComponentSerializer.legacyAmpersand().deserialize(desc)), frozen);
        messageComponent = component1 != null ? component1 : Component.empty();
        descriptionComponent = component2 != null ? component2 : Component.empty();
    }

    @Override
    protected void sent(boolean add) {
        ClientboundUpdateAdvancementsPacket packet;

        if (add) {
            Advancement advancement = new Advancement(
                Optional.empty(),
                Optional.of(new DisplayInfo(
                    CraftItemStack.asNMSCopy(new ItemStack(icon)),
                    messageComponent,
                    descriptionComponent,
                    Optional.empty(),
                    AdvancementType.TASK,
                    true,
                    false,
                    true
                )),
                AdvancementRewards.EMPTY,
                Collections.emptyMap(),
                SIMPLE_REQUIREMENT,
                true
            );

            AdvancementProgress progress = new AdvancementProgress();
            progress.update(SIMPLE_REQUIREMENT);
            progress.grantProgress("1");
            packet = new ClientboundUpdateAdvancementsPacket(
                false,
                Collections.singleton(new AdvancementHolder(resourceLocation, advancement)),
                Collections.emptySet(),
                Collections.singletonMap(resourceLocation, progress)
            );
        } else {
            packet = new ClientboundUpdateAdvancementsPacket(
                false,
                Collections.emptySet(),
                Collections.singleton(resourceLocation),
                Collections.emptyMap()
            );
        }
        CraftPlayer player = (CraftPlayer) this.player;
        player.getHandle().connection.send(packet);
    }
}