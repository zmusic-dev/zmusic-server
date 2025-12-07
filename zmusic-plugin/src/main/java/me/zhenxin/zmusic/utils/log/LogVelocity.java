package me.zhenxin.zmusic.utils.log;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import me.zhenxin.zmusic.ZMusicVelocity;
import me.zhenxin.zmusic.component.adapter.AdventureComponentAdapter;
import me.zhenxin.zmusic.config.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class LogVelocity implements Log {

    private final AdventureComponentAdapter adapter = AdventureComponentAdapter.INSTANCE;

    @Override
    public void sendNormalMessage(String message) {
        CommandSource console = ZMusicVelocity.server.getConsoleCommandSource();
        console.sendMessage(adapter.fromText(Config.prefix).append(
                Component.text(message).color(NamedTextColor.GREEN)));
    }

    @Override
    public void sendDebugMessage(String message) {
        if (Config.debug) {
            CommandSource console = ZMusicVelocity.server.getConsoleCommandSource();
            console.sendMessage(adapter.fromText(Config.prefix).append(
                    Component.text("[Debug] " + message).color(NamedTextColor.YELLOW)));
        }
    }

    @Override
    public void sendErrorMessage(String message) {
        CommandSource console = ZMusicVelocity.server.getConsoleCommandSource();
        console.sendMessage(adapter.fromText(Config.prefix).append(
                Component.text(message).color(NamedTextColor.RED)));
    }

    @Override
    public Object getSender() {
        return ZMusicVelocity.server.getConsoleCommandSource();
    }
}
