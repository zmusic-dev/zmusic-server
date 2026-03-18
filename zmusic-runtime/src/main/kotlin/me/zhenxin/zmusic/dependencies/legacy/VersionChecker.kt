package me.zhenxin.zmusic.dependencies.legacy

import java.io.File
import java.util.concurrent.TimeUnit

object VersionChecker {
    private val checkFile = File("version.lock")

    @JvmStatic
    fun isOutdated(): Boolean {
        return System.currentTimeMillis() - getLatestCheckTime() > TimeUnit.DAYS.toMillis(7)
    }

    @JvmStatic
    fun getLatestCheckTime(): Long {
        return checkFile.lastModified()
    }

    @JvmStatic
    fun updateCheckTime() {
        if (checkFile.exists()) {
            checkFile.setLastModified(System.currentTimeMillis())
            return
        }
        checkFile.createNewFile()
    }
}
