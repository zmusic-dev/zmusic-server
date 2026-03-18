dependencies {
    api(project(":zmusic-core"))

    compileOnly(libs.spigot)
    compileOnly(libs.placeholderapi)
    implementation(libs.bstats.bukkit)
    compileOnly(libs.adventure)
    compileOnly(libs.adventure.text.serializer.legacy)
}

tasks.processResources {
    inputs.property("version", version)

    filesMatching("plugin.yml") {
        expand(mapOf("version" to version))
    }
}
