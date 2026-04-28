package me.zhenxin.zmusic.command.permission

/**
 * 权限常量
 */
object Permissions {
    val USE = Permission("zmusic.use", "允许使用 ZMusic 根命令")
    val HELP = Permission("zmusic.help", "允许查看帮助")
    val INFO = Permission("zmusic.info", "允许查看插件状态")
    val PLAY = Permission("zmusic.play", "允许播放音乐")
    val SEARCH = Permission("zmusic.search", "允许搜索音乐")
    val STOP = Permission("zmusic.stop", "允许停止播放")
    val RELOAD = Permission("zmusic.reload", "允许重载配置")
    val PLAYLIST = Permission("zmusic.playlist", "允许管理歌单")
    val ADMIN = Permission("zmusic.admin", "允许使用所有管理命令")
}
