import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    alias(libs.plugins.kyori.blossom)
}

dependencies {
    implementation(libs.lucko.jar.relocator)

    compileOnly(libs.google.guava)
    compileOnly(libs.google.gson)

    compileOnly(libs.bundles.maven.resolver)

    compileOnly(libs.netty.buffer)
}

sourceSets {
    main {
        blossom {
            javaSources {
                property("pluginVersion", project.version.toString())
                property("pluginVersionCode", versionCode())
                property("kotlinVersion", libs.versions.kotlin.get())
                property("hutoolVersion", libs.versions.hutool.get())
                property("nettyVersion", libs.versions.netty.get())
                property("nightConfigVersion", libs.versions.nightconfig.get())
                property("bstatsVersion", libs.versions.bstats.get())
            }
        }
    }
}

tasks.shadowJar {
    enabled = false
}

fun versionCode(): String {
    val runId = System.getenv("GITHUB_RUN_ID") ?: ""
    if (runId.isNotEmpty()) {
        return runId
    }

    val time = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyMMddHHmm")
    return time.format(formatter)
}