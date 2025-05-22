dependencies {
    api(project(":zmusic-core"))

    compileOnly(libs.bungeecord)
    compileOnly(libs.bstats.bungeecord)
}

tasks.processResources {
    inputs.property("version", version)

    filesMatching("bungee.yml") {
        expand(mapOf("version" to version))
    }
}
