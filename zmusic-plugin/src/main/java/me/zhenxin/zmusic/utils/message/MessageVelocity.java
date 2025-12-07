package me.zhenxin.zmusic.utils.message;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import me.zhenxin.zmusic.component.ZComponent;
import me.zhenxin.zmusic.component.adapter.AdventureComponentAdapter;
import me.zhenxin.zmusic.config.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;

import java.time.Duration;

public class MessageVelocity implements Message {

    private final AdventureComponentAdapter adapter = AdventureComponentAdapter.INSTANCE;

    @Override
    public void sendNormalMessage(String message, Object playerObj) {
        if (playerObj instanceof Player) {
            Player player = (Player) playerObj;
            player.sendMessage(adapter.fromText(Config.prefix).append(
                    Component.text(message).color(NamedTextColor.GREEN)));
        } else if (playerObj instanceof CommandSource) {
            CommandSource source = (CommandSource) playerObj;
            source.sendMessage(adapter.fromText(Config.prefix).append(
                    Component.text(message).color(NamedTextColor.GREEN)));
        }
    }

    @Override
    public void sendErrorMessage(String message, Object playerObj) {
        if (playerObj instanceof Player) {
            Player player = (Player) playerObj;
            player.sendMessage(adapter.fromText(Config.prefix).append(
                    Component.text(message).color(NamedTextColor.RED)));
        } else if (playerObj instanceof CommandSource) {
            CommandSource source = (CommandSource) playerObj;
            source.sendMessage(adapter.fromText(Config.prefix).append(
                    Component.text(message).color(NamedTextColor.RED)));
        }
    }

    @Override
    public void sendJsonMessage(ZComponent message, Object playerObj) {
        if (playerObj instanceof Player) {
            Player player = (Player) playerObj;
            player.sendMessage(adapter.adapt(message));
        }
    }

    @Override
    public void sendActionBarMessage(ZComponent message, Object playerObj) {
        if (playerObj instanceof Player) {
            Player player = (Player) playerObj;
            player.sendActionBar(adapter.adapt(message));
        }
    }

    @Override
    public void sendTitleMessage(String title, String subTitle, Object playerObj) {
        if (playerObj instanceof Player) {
            Player player = (Player) playerObj;
            Title.Times times = Title.Times.times(
                    Duration.ZERO,
                    Duration.ofMillis(10000),
                    Duration.ofMillis(1000)
            );
            player.showTitle(Title.title(
                    adapter.fromText(title),
                    adapter.fromText(subTitle),
                    times
            ));
        }
    }

    @Override
    public void sendNull(Object playerObj) {
        sendNormalMessage("输入 /zm help 查看帮助.", playerObj);
    }
}
