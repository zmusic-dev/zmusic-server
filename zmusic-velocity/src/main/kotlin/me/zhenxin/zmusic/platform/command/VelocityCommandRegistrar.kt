package me.zhenxin.zmusic.platform.command

import com.velocitypowered.api.command.CommandManager
import com.velocitypowered.api.command.CommandMeta
import com.velocitypowered.api.command.SimpleCommand
import com.velocitypowered.api.proxy.ProxyServer
import me.zhenxin.zmusic.command.ZMusicCommandManager
import me.zhenxin.zmusic.platform.entity.VelocityCommandSender

class VelocityCommandRegistrar(private val server: ProxyServer) {

    fun register() {
        val commandManager: CommandManager = server.commandManager
        val meta: CommandMeta = commandManager.metaBuilder(ZMusicCommandManager.ROOT_COMMAND)
            .aliases("music", "zm")
            .build()
        commandManager.register(meta, RootCommand())
    }

    private class RootCommand : SimpleCommand {

        override fun execute(invocation: SimpleCommand.Invocation) {
            ZMusicCommandManager.execute(
                VelocityCommandSender(invocation.source()),
                ZMusicCommandManager.ROOT_COMMAND,
                invocation.arguments()
            )
        }

        override fun suggest(invocation: SimpleCommand.Invocation): List<String> {
            return ZMusicCommandManager.suggest(
                VelocityCommandSender(invocation.source()),
                ZMusicCommandManager.ROOT_COMMAND,
                invocation.arguments()
            )
        }
    }
}
