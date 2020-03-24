import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "0.10.1"
}

dependencies {
    implementation(kotlin("gradle-plugin"))
    implementation(kotlin("gradle-plugin-api"))
}

gradlePlugin {
    plugins {
        register("detektPlugin") {
            id = "io.gitlab.arturbosch.detekt"
            implementationClass = "io.gitlab.arturbosch.detekt.DetektPlugin"
        }
    }
}

tasks.validatePlugins {
    enableStricterValidation.set(true)
}

pluginBundle {
    website = "https://arturbosch.github.io/detekt"
    vcsUrl = "https://github.com/arturbosch/detekt"
    description = "Static code analysis for Kotlin"
    tags = listOf("kotlin", "detekt", "code-analysis", "linter", "codesmells")

    (plugins) {
        "detektPlugin" {
            id = "io.gitlab.arturbosch.detekt"
            displayName = "Static code analysis for Kotlin"
        }
    }
}

val generateDefaultDetektVersionFile by tasks.registering {
    val defaultDetektVersionFile =
        File("$buildDir/generated/src/io/gitlab/arturbosch/detekt", "PluginVersion.kt")

    outputs.file(defaultDetektVersionFile)

    doFirst {
        defaultDetektVersionFile.parentFile.mkdirs()
        defaultDetektVersionFile.writeText("""
            package io.gitlab.arturbosch.detekt

            internal const val DEFAULT_DETEKT_VERSION = "$version"

            """.trimIndent()
        )
    }
}

sourceSets.main.get().java.srcDir("$buildDir/generated/src")

tasks.compileKotlin {
    dependsOn(generateDefaultDetektVersionFile)
}
