package me.zhenxin.zmusic.platform.service

import com.velocitypowered.api.proxy.ProxyServer
import me.zhenxin.zmusic.api.entity.ZPlayer
import me.zhenxin.zmusic.api.service.PlayerService
import me.zhenxin.zmusic.platform.entity.VelocityPlayer
import java.util.UUID

/**
 * Velocity 玩家服务实现
 *
 * @author 真心
 * @since 2024/12/9
 */
class VelocityPlayerService(private val server: ProxyServer) : PlayerService {

    override fun getOnlinePlayers(): Collection<ZPlayer> {
        return server.allPlayers.map { VelocityPlayer(it) }
    }

    override fun getPlayer(uuid: UUID): ZPlayer? {
        return server.getPlayer(uuid).map { VelocityPlayer(it) }.orElse(null)
    }

    override fun getPlayer(name: String): ZPlayer? {
        return server.getPlayer(name).map { VelocityPlayer(it) }.orElse(null)
    }
}
