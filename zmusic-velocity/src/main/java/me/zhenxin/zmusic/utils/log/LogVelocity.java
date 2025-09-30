package me.zhenxin.zmusic.utils.log;

import com.velocitypowered.api.command.CommandSource;
import me.zhenxin.zmusic.config.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class LogVelocity implements Log {

    private final CommandSource sender;

    public LogVelocity(CommandSource sender) {
        this.sender = sender;
    }

    @Override
    public void sendNormalMessage(String message) {
        Component component = Component.text(Config.prefix)
                .append(Component.text(message).color(NamedTextColor.GREEN));
        sender.sendMessage(component);
    }

    @Override
    public void sendDebugMessage(String message) {
        if (Config.debug) {
            Component component = Component.text(Config.prefix)
                    .append(Component.text("[Debug] ").color(NamedTextColor.YELLOW))
                    .append(Component.text(message).color(NamedTextColor.YELLOW));
            sender.sendMessage(component);
        }
    }

    @Override
    public void sendErrorMessage(String message) {
        Component component = Component.text(Config.prefix)
                .append(Component.text(message).color(NamedTextColor.RED));
        sender.sendMessage(component);
    }

    @Override
    public Object getSender() {
        return sender;
    }
}