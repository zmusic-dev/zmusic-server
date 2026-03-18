dependencies {
    api(project(":zmusic-core"))

    compileOnly(libs.bungeecord)
    implementation(libs.bstats.bungeecord)
    compileOnly(libs.adventure)
    compileOnly(libs.adventure.text.serializer.legacy)
}

tasks.processResources {
    inputs.property("version", version)

    filesMatching("bungee.yml") {
        expand(mapOf("version" to version))
    }
}
