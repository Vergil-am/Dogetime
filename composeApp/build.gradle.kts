import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

// Local-only secrets: copy secrets.properties.example to secrets.properties (git-ignored)
// and fill in the values. They become a generated `dev.dogetime.Secrets` object,
// so code can read them as Secrets.TEST_API_KEY, the way .env works in JS apps.
val secretsFile = rootProject.file("secrets.properties")
val secrets = Properties().apply {
    if (secretsFile.exists()) secretsFile.inputStream().use { load(it) }
}
val generatedSecretsDir = layout.buildDirectory.dir("generated/secrets/commonMain/kotlin")

fun String.escapeForKotlin() = replace("\\", "\\\\").replace("\"", "\\\"").replace("$", "\\$")

val generateSecrets = tasks.register("generateSecrets") {
    val values = secrets.stringPropertyNames().sorted().associateWith { secrets.getProperty(it) }
    inputs.property("secrets", values)
    outputs.dir(generatedSecretsDir)
    doLast {
        val packageDir = generatedSecretsDir.get().asFile.resolve("dev/dogetime")
        packageDir.mkdirs()
        val members = values.entries.joinToString("\n") { (key, value) ->
            "    const val $key = \"${value.escapeForKotlin()}\""
        }
        packageDir.resolve("Secrets.kt").writeText("package dev.dogetime\n\nobject Secrets {\n$members\n}\n")
    }
}

kotlin {
    android {
        namespace = "dev.dogetime.shared"
        compileSdk = 37
        minSdk = 24
    }

    // Shared iOS source set, created explicitly so src/iosMain is compiled into the framework.
    val iosMain = sourceSets.create("iosMain") {
        dependsOn(sourceSets.getByName("commonMain"))
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
        iosTarget.compilations.getByName("main").defaultSourceSet.dependsOn(iosMain)
    }

    jvm("desktop")

    // Android and desktop both run on the JVM, so the torrent player (libtorrent4j) is shared there.
    val jvmMain = sourceSets.create("jvmMain") {
        dependsOn(sourceSets.getByName("commonMain"))
    }
    sourceSets.getByName("androidMain").dependsOn(jvmMain)
    sourceSets.getByName("desktopMain").dependsOn(jvmMain)

    // Registering the task (not just its folder) makes every task that reads these sources run it first.
    sourceSets.getByName("commonMain").kotlin.srcDir(generateSecrets)

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)
            implementation(libs.compose.webview)
            implementation(libs.haze.glass)
            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)
        }
        jvmMain.dependencies {
            implementation(libs.libtorrent4j)
            implementation(libs.ktor.server.cio)
        }
        getByName("androidMain").dependencies {
            implementation(libs.libtorrent4j.android.arm64)
            implementation(libs.libtorrent4j.android.arm)
            implementation(libs.libtorrent4j.android.x64)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.webkit)
            implementation(libs.mpv.android)
            implementation(libs.ktor.client.okhttp)
        }
        getByName("desktopMain").dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.jna)
            // libtorrent's native library for the OS this is built on, like compose.desktop.currentOs.
            val osName = System.getProperty("os.name")
            implementation(
                when {
                    osName.startsWith("Windows") -> libs.libtorrent4j.windows
                    osName.contains("Mac") -> libs.libtorrent4j.macos
                    else -> libs.libtorrent4j.linux
                },
            )
            implementation(libs.ktor.client.okhttp)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

compose.desktop {
    application {
        mainClass = "dev.dogetime.MainKt"

        // The desktop WebView (KCEF/JCEF) reaches into AWT internals to embed Chromium.
        jvmArgs("--add-opens", "java.desktop/sun.awt=ALL-UNNAMED")
        jvmArgs("--add-opens", "java.desktop/java.awt.peer=ALL-UNNAMED")
        // libtorrent4j and JNA (mpv) load native libraries, which newer JDKs warn about and will block.
        jvmArgs("--enable-native-access=ALL-UNNAMED")
        val osName = System.getProperty("os.name")
        if (osName.contains("Linux")) {
            jvmArgs("--add-opens", "java.desktop/sun.awt.X11=ALL-UNNAMED")
        }
        if (osName.contains("Mac")) {
            jvmArgs("--add-opens", "java.desktop/sun.lwawt=ALL-UNNAMED")
            jvmArgs("--add-opens", "java.desktop/sun.lwawt.macosx=ALL-UNNAMED")
        }

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "DogeTime"
            packageVersion = "1.0.0"
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

// Room's annotation processor runs once per target.
dependencies {
    listOf("kspAndroid", "kspIosArm64", "kspIosSimulatorArm64", "kspDesktop").forEach { configuration ->
        add(configuration, libs.room.compiler)
    }
}
