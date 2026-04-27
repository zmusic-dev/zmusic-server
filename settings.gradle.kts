rootProject.name = "zmusic-plugin"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

include(
    "zmusic-bukkit",
    "zmusic-bungee",
    "zmusic-core",
    "zmusic-runtime",
    "zmusic-velocity"
)
