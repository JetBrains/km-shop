import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
    kotlin("android")
    kotlin("plugin.compose")
    kotlin("plugin.serialization")
    id("kotlin-parcelize")
}

kotlin{
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)

        freeCompilerArgs.add("-Xopt-in=kotlin.RequiresOptIn")
        val customLanguageVersion = findProperty("kotlin_language_version")
        println("Module: ${project.name}. Custom LanguageVersion: $customLanguageVersion")
        customLanguageVersion?.let {
            languageVersion.set(KotlinVersion.fromVersion(it.toString()))
        }

        val customApiVersion = findProperty("kotlin_api_version")
        println("Module: ${project.name}. Custom ApiVersion: $customApiVersion")
        customApiVersion?.let {
            apiVersion.set(KotlinVersion.fromVersion(it.toString()))
        }
    }
}

android {
    namespace = "com.keygenqt.shop.android"

    compileSdk = findProperty("compileSdk").toString().toInt()

    defaultConfig {
        minSdk = findProperty("minSdk").toString().toInt()
        targetSdk = findProperty("targetSdk").toString().toInt()
        applicationId = "com.keygenqt.shop.android"
        versionCode = 1
        versionName = "0.9.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    sourceSets {
        getByName("main").let { data ->
            data.res.setSrcDirs(emptySet<String>())
            file("src/main/res").listFiles()?.toList()?.forEach { dir ->
                data.res.srcDir(dir)
            }
        }
    }

    packagingOptions {
        resources {
            excludes.add("**/attach_hotspot_windows.dll")
            excludes.add("META-INF/licenses/**")
            excludes.add("META-INF/AL2.0")
            excludes.add("META-INF/LGPL2.1")
        }
    }
}

dependencies {
    // kmm
    implementation(project(":shared"))
    // versionCatalogs
    implementation(androidApp.bundles.android)
    kapt(androidApp.bundles.androidKapt)
    // libs
//    if (findProperty("internalLibrariesEnable").toString().toBoolean()) {
//        implementation(project(":compose-routing"))
//    } else {
//        implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
//    }
}

configurations.all {
    resolutionStrategy {
        val kotlin_version: String by project
        force("org.jetbrains.kotlin:kotlin-metadata-jvm:$kotlin_version")
    }
}