package me.zhenxin.zmusic.platform.service

import me.zhenxin.zmusic.api.entity.ZPlayer
import me.zhenxin.zmusic.api.service.PlayerService
import me.zhenxin.zmusic.platform.entity.BungeePlayer
import net.md_5.bungee.api.ProxyServer
import java.util.UUID

/**
 * BungeeCord 玩家服务实现
 *
 * @author 真心
 * @since 2024/12/9
 */
class BungeePlayerService : PlayerService {

    override fun getOnlinePlayers(): Collection<ZPlayer> {
        return ProxyServer.getInstance().players.map { BungeePlayer(it) }
    }

    override fun getPlayer(uuid: UUID): ZPlayer? {
        return ProxyServer.getInstance().getPlayer(uuid)?.let { BungeePlayer(it) }
    }

    override fun getPlayer(name: String): ZPlayer? {
        return ProxyServer.getInstance().getPlayer(name)?.let { BungeePlayer(it) }
    }
}
