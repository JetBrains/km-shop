import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // android
    id("com.google.dagger.hilt.android").apply(false)
    id("com.android.application").apply(false)
    id("com.android.library").apply(false)
    kotlin("android").apply(false)

    // common
    kotlin("multiplatform").apply(false)
    kotlin("plugin.serialization").apply(false)
    id("dev.petuska.npm.publish").apply(false)

    // gradle
//    id("com.diffplug.spotless")
    id("org.jetbrains.dokka")
}

buildscript {

    // https://github.com/yshrsmz/BuildKonfig
    val buildkonfigVersion = "0.13.3"

    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    dependencies {
        classpath("com.codingfeline.buildkonfig:buildkonfig-gradle-plugin:$buildkonfigVersion")
        classpath("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.9.0")
    }
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaMultiModuleTask>().configureEach {
    outputDirectory.set(rootDir.resolve("docs/dokka"))
    failOnWarning.set(true)
}

subprojects {

    apply(plugin = "org.jetbrains.dokka")
//    apply(plugin = "com.diffplug.spotless")

//    spotless {
//        kotlin {
//            target("**/*.kt")
//            targetExclude("**/build/**")
//            ktlint("0.50.0")
//                .userData(
//                    mapOf(
//                        "disabled_rules" to "no-wildcard-imports",
//                    )
//                )
//            licenseHeaderFile("$rootDir/copyright.txt")
//        }
//        format("misc") {
//            target("**/*.gradle", "**/*.md", "**/.gitignore")
//            trimTrailingWhitespace()
//            indentWithSpaces()
//            endWithNewline()
//        }
//    }

    tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial>().configureEach {
        suppressInheritedMembers.set(true)
        dokkaSourceSets {
            configureEach {
                includes.from("dokka.md")
                includeNonPublic.set(true)
            }
        }
    }
    val kotlin_version: String? by project
    kotlin_version?.also {
        configurations.all {
            resolutionStrategy.eachDependency {
                if (requested.group == "androidx.compose.compiler" && requested.name == "compiler") {
                    useTarget("org.jetbrains.kotlin:kotlin-compose-compiler-plugin-embeddable:$it")
                    because("Temporary WA for compose compiler migration")
                }
            }
        }
        tasks.withType<KotlinCompile>().configureEach {
            kotlinOptions {
                freeCompilerArgs += listOf(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:" +
                            "suppressKotlinVersionCompatibilityCheck=$it",
                    "-Xmetadata-version=1.8" //WA for compatibility with Hilt see
                    // https://jetbrains.slack.com/archives/C04857B0BNE/p1688641699713959?thread_ts=1688639092.588919&cid=C04857B0BNE
                )
            }
        }
    }

}