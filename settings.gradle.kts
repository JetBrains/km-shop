pluginManagement {
    @Suppress("LocalVariableName") val kotlin_repo_url: String? by settings
    @Suppress("LocalVariableName") val kotlin_version: String? by settings

    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        kotlin_repo_url?.also { maven(it) }
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
    }

    // https://kotlinlang.org/docs/releases.html#release-details
    val kotlinVersion = kotlin_version ?: "2.3.10"
//    val kotlinVersion = kotlin_version ?: "1.9.0"
    // https://github.com/diffplug/spotless/tags
    val spotlessVersion = "6.20.0"
    // https://github.com/Kotlin/dokka/tags
    val dokkaVersions = "1.7.20"
    // https://github.com/mpetuska/npm-publish
    val npmPublishVersion = "3.4.3"
    // https://developer.android.com/studio/releases/gradle-plugin#updating-plugin
    val androidVersion = "8.5.2"
    // https://developer.android.com/training/dependency-injection/hilt-android
    val hiltVersion = "2.57.1"

    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("android") version kotlinVersion
        kotlin("multiplatform") version kotlinVersion
        kotlin("plugin.compose") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion

        id("com.google.dagger.hilt.android") version hiltVersion
        id("com.android.application") version androidVersion
        id("com.android.library") version androidVersion
        id("dev.petuska.npm.publish") version npmPublishVersion

        // gradle
        id("com.diffplug.spotless") version spotlessVersion
        id("org.jetbrains.dokka") version dokkaVersions
    }
}

dependencyResolutionManagement {
    @Suppress("LocalVariableName") val kotlin_repo_url: String? by settings

    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
        kotlin_repo_url?.also { maven(it) }
    }

    versionCatalogs {
        create("kmModule") {
            from(files("dependencies/libs.km.toml"))
        }
        create("cliApp") {
            from(files("dependencies/libs.cli.toml"))
        }
        create("apiApp") {
            from(files("dependencies/libs.api.toml"))
        }
        create("androidApp") {
            from(files("dependencies/libs.android.toml"))
        }
        create("pcApp") {
            from(files("dependencies/libs.pc.toml"))
        }
    }
}


rootProject.name = "Shop"

include("shop:mobile:android")
//include("shop:server:cli")
include("shop:server:api")
//include("shop:pc:client")
include(":shared")
