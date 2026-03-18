package me.zhenxin.zmusic.dependencies

import me.lucko.jarrelocator.Relocation

data class JarRelocation(
    val pattern: String,
    val relocatedPattern: String,
) {
    fun toRelocation(): Relocation {
        return Relocation(pattern, relocatedPattern)
    }
}
