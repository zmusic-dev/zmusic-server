package me.zhenxin.zmusic.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import me.zhenxin.zmusic.ZMusicVelocity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CmdVelocity implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        Cmd.cmd(source, args);
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        Iterable<String> suggestions = Cmd.tab(source, args);
        List<String> result = new ArrayList<>();
        suggestions.forEach(result::add);
        return result;
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return true; // 权限在 Cmd 内部处理
    }
}
