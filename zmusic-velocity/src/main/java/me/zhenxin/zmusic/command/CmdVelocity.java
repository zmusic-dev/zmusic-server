package me.zhenxin.zmusic.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CmdVelocity implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        Cmd.cmd(source, args);
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        return CompletableFuture.completedFuture(new ArrayList<>(Cmd.tab(source, args)));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return true; // Allow all users to execute the command, permissions are handled internally
    }
}