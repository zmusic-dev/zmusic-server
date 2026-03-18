package me.zhenxin.zmusic.dependencies.legacy

class DependencyVersion(private val version: String) : Comparable<DependencyVersion> {
    private val parts = version.split(Regex("[^0-9]"))
        .filter(String::isNotEmpty)
        .map(String::toInt)

    override fun compareTo(other: DependencyVersion): Int {
        val us = parts.iterator()
        val them = other.parts.iterator()
        while (us.hasNext() && them.hasNext()) {
            val diff = us.next().compareTo(them.next())
            if (diff != 0) {
                return diff
            }
        }
        return when {
            us.hasNext() -> 1
            them.hasNext() -> -1
            else -> 0
        }
    }

    override fun toString(): String {
        return version
    }
}
