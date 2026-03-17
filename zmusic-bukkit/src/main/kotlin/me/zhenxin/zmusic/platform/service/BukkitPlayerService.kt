package me.zhenxin.zmusic.platform.service

import me.zhenxin.zmusic.api.entity.ZPlayer
import me.zhenxin.zmusic.api.service.PlayerService
import me.zhenxin.zmusic.platform.entity.BukkitPlayer
import org.bukkit.Bukkit
import java.util.UUID

/**
 * Bukkit 玩家服务实现
 *
 * @author 真心
 * @since 2024/12/9
 */
class BukkitPlayerService : PlayerService {

    override fun getOnlinePlayers(): Collection<ZPlayer> {
        return Bukkit.getOnlinePlayers().map { BukkitPlayer(it) }
    }

    override fun getPlayer(uuid: UUID): ZPlayer? {
        return Bukkit.getPlayer(uuid)?.let { BukkitPlayer(it) }
    }

    override fun getPlayer(name: String): ZPlayer? {
        return Bukkit.getPlayer(name)?.let { BukkitPlayer(it) }
    }
}
