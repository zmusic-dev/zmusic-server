import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    api(project(":zmusic-core"))
    compileOnly(libs.velocity)
    implementation(libs.bstats.velocity)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

tasks.processResources {
    inputs.property("version", version)

    filesMatching("velocity-plugin.json") {
        expand(mapOf("version" to version))
    }
}