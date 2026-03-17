dependencies {
    api(project(":zmusic-runtime"))

    compileOnly(libs.adventure)
    compileOnly(libs.adventure.text.serializer.legacy)

    compileOnly(libs.bundles.hutool)
    compileOnly(libs.bundles.nightconfig)
}

tasks.shadowJar {
    enabled = false
}