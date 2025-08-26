import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("io.ktor.plugin") version "2.2.3"
}

group = "com.keygenqt.shop.api"
version = "0.9.0"

application {
    mainClass.set("com.keygenqt.shop.api.ApplicationKt")
}

kotlin{
    compilerOptions {
        findProperty("kotlin_language_version")?.let {
            println("Module: ${project.name}. Custom LV: $it")
            languageVersion.set(KotlinVersion.fromVersion(it.toString()))
        }

        findProperty("kotlin_api_version")?.let {
            println("Module: ${project.name}. Custom AV: $it")
            apiVersion.set(KotlinVersion.fromVersion(it.toString()))
        }
    }
}

dependencies {
    implementation(project(":shared"))
    // Ktor
    implementation(apiApp.bundles.ktor)

    implementation("io.ktor:ktor-server-websockets:2.1.3")
}

ktor {
    fatJar {
        archiveFileName.set("shop-api.jar")
    }
}