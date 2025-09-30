package me.zhenxin.zmusic.utils.message;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import me.zhenxin.zmusic.config.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import net.md_5.bungee.api.chat.TextComponent;

import java.time.Duration;

public class MessageVelocity implements Message {

    @Override
    public void sendNormalMessage(String message, Object playerObj) {
        CommandSource sender = (CommandSource) playerObj;
        Component component = Component.text(Config.prefix)
                .append(Component.text(message).color(NamedTextColor.GREEN));
        sender.sendMessage(component);
    }

    @Override
    public void sendErrorMessage(String message, Object playerObj) {
        CommandSource sender = (CommandSource) playerObj;
        Component component = Component.text(Config.prefix)
                .append(Component.text(message).color(NamedTextColor.RED));
        sender.sendMessage(component);
    }

    @Override
    public void sendJsonMessage(TextComponent message, Object playerObj) {
        Player player = (Player) playerObj;
        // Convert TextComponent to Adventure Component
        String legacyText = message.toLegacyText();
        Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(legacyText);
        player.sendMessage(component);
    }

    @Override
    public void sendActionBarMessage(TextComponent message, Object playerObj) {
        Player player = (Player) playerObj;
        String legacyText = message.toLegacyText();
        Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(legacyText);
        player.sendActionBar(component);
    }

    @Override
    public void sendTitleMessage(String title, String subTitle, Object playerObj) {
        Player player = (Player) playerObj;
        Component titleComponent = Component.text(title);
        Component subtitleComponent = Component.text(subTitle);
        
        Title titleMessage = Title.title(
                titleComponent,
                subtitleComponent,
                Title.Times.times(Duration.ZERO, Duration.ofSeconds(10), Duration.ofSeconds(1))
        );
        player.showTitle(titleMessage);
    }

    @Override
    public void sendNull(Object playerObj) {
        CommandSource sender = (CommandSource) playerObj;
        Component component = Component.text(Config.prefix)
                .append(Component.text("输入 /zm help 查看帮助.").color(NamedTextColor.GREEN));
        sender.sendMessage(component);
    }
}