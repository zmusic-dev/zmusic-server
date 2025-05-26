import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

dependencies {
    api(project(":zmusic-core"))
    compileOnly(libs.velocity)
    implementation(libs.bstats.velocity)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

tasks.processResources {
    inputs.property("version", version)

    filesMatching("velocity-plugin.json") {
        expand(mapOf("version" to version))
    }
}