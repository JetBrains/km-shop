import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import java.util.*

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("dev.petuska.npm.publish")
}

kotlin {
    android()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

    js(IR) {
        moduleName = "shared"
        version = "0.0.1"
        nodejs()
        binaries.library()
    }

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../shop/mobile/ios/Podfile")
        framework {
            baseName = "shared"
            isStatic = true
        }
    }

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

    sourceSets {
        all {
            languageSettings.apply {
                optIn("kotlin.js.ExperimentalJsExport")
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting

        val commonMain by getting {
            dependencies {
                implementation(kmModule.bundles.common)
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0-RC")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(kmModule.bundles.android)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kmModule.bundles.jvm)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(kmModule.bundles.js)
            }
        }
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation(kmModule.bundles.ios)
            }
        }
    }
}

android {
    namespace = "com.keygenqt.shop"
    compileSdk = findProperty("compileSdk").toString().toInt()
    defaultConfig {
        minSdk = findProperty("minSdk").toString().toInt()
        targetSdk = findProperty("targetSdk").toString().toInt()
    }
    lint{
        quiet = true
        checkReleaseBuilds = false
    }
}

npmPublish {
    packages {
        named("js") {
            packageJson {
                version.set("0.0.1")
            }
        }
    }
}

rootProject.plugins.withType(org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin::class.java) {
    rootProject.the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>().nodeVersion = "17.9.1"
}