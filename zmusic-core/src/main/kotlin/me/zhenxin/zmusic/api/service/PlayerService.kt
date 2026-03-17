package me.zhenxin.zmusic.api.service

import me.zhenxin.zmusic.api.entity.ZPlayer
import java.util.UUID

/**
 * 玩家服务接口
 *
 * @author 真心
 * @since 2024/12/9
 */
interface PlayerService {
    /**
     * 获取所有在线玩家
     */
    fun getOnlinePlayers(): Collection<ZPlayer>

    /**
     * 根据 UUID 获取玩家
     */
    fun getPlayer(uuid: UUID): ZPlayer?

    /**
     * 根据名称获取玩家
     */
    fun getPlayer(name: String): ZPlayer?

    /**
     * 获取在线玩家数量
     */
    fun getOnlinePlayerCount(): Int = getOnlinePlayers().size
}
