dependencies {
    api(project(":zmusic-core"))

    compileOnly(libs.spigot)
    compileOnly(libs.bstats.bukkit)
}

tasks.processResources {
    inputs.property("version", version)

    filesMatching("plugin.yml") {
        expand(mapOf("version" to version))
    }
}
