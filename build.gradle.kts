plugins {
    // android
    id("com.google.dagger.hilt.android").apply(false)
    id("com.android.application").apply(false)
    id("com.android.library").apply(false)
    kotlin("android").apply(false)
    kotlin("plugin.compose").apply(false)

    // common
    kotlin("multiplatform").apply(false)
    kotlin("plugin.serialization").apply(false)
    id("dev.petuska.npm.publish").apply(false)

    id("com.github.johnrengelman.shadow") version "8.1.1"

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
        val kotlin_version: String by project
        classpath("com.codingfeline.buildkonfig:buildkonfig-gradle-plugin:$buildkonfigVersion")
        classpath("org.jetbrains.kotlin:kotlin-metadata-jvm:$kotlin_version")
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

    // !! infrastructure for builds as a Kotlin user project
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>().configureEach kotlinCompilationTask@ {
        // ("unconditionally pass the -Xreport-all-warnings CLI option to the Kotlin compiler")
        // ("unconditionally pass the -Xrender-internal-diagnostic-names CLI option to the Kotlin compiler")
        compilerOptions {
            // output reported warnings even in the presence of reported errors
            freeCompilerArgs.add("-Xreport-all-warnings")
            logger.info("<KUP> ${this@kotlinCompilationTask.path} : added -Xreport-all-warnings")
            // output kotlin.git-searchable names of reported diagnostics
            freeCompilerArgs.add("-Xrender-internal-diagnostic-names")
            logger.info("<KUP> ${this@kotlinCompilationTask.path} : added -Xrender-internal-diagnostic-names")
        }
    }

// !! infrastructure for builds as a Kotlin user project
// ("implement a Gradle property for passing additional CLI options to the Kotlin compiler")
    val kotlinAdditionalCliOptions = providers.gradleProperty("kotlin_additional_cli_options")
        .map { if (it.isNotBlank()) it.split(" ") else emptyList() } // i sure hope that this level of parsing is in fact enough
        .orNull
    if (kotlinAdditionalCliOptions != null) {
        tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>().configureEach kotlinCompilationTask@ {
            compilerOptions {
                freeCompilerArgs.addAll(kotlinAdditionalCliOptions)
                logger.info("<KUP> ${this@kotlinCompilationTask.path} : added ${kotlinAdditionalCliOptions.joinToString(" ")}")
            }
        }
    }

// !! infrastructure for builds as a Kotlin user project
// ("unconditionally disable the -Werror compiler CLI option")
    afterEvaluate {
        tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>().configureEach kotlinCompilationTask@ {
            compilerOptions {
                allWarningsAsErrors.set(false)
                freeCompilerArgs.set(
                    freeCompilerArgs.getOrElse(emptyList()).filterNot { it == "-Werror" }
                )
                logger.info("<KUP> ${this@kotlinCompilationTask.path} : disabled -Werror")
            }
        }
    }
}
