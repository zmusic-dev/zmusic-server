package me.zhenxin.zmusic.utils.player;

import com.velocitypowered.api.command.CommandSource;
import me.zhenxin.zmusic.ZMusicVelocity;

import java.util.ArrayList;
import java.util.List;

public class PlayerVelocity implements Player {

    @Override
    public boolean hasPermission(Object playerObj, String permission) {
        com.velocitypowered.api.proxy.Player player = (com.velocitypowered.api.proxy.Player) playerObj;
        return player.hasPermission(permission);
    }

    @Override
    public List<Object> getOnlinePlayerList() {
        return new ArrayList<>(ZMusicVelocity.plugin.getServer().getAllPlayers());
    }

    @Override
    public boolean isOnline(Object playerObj) {
        com.velocitypowered.api.proxy.Player player = (com.velocitypowered.api.proxy.Player) playerObj;
        return player.isActive();
    }

    @Override
    public boolean isPlayer(Object sender) {
        return sender instanceof com.velocitypowered.api.proxy.Player;
    }

    @Override
    public String getName(Object sender) {
        if (sender instanceof com.velocitypowered.api.proxy.Player) {
            return ((com.velocitypowered.api.proxy.Player) sender).getUsername();
        } else if (sender instanceof CommandSource) {
            return "Console";
        }
        return sender.toString();
    }
}
