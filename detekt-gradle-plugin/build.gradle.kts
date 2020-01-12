plugins {
    `java-gradle-plugin`
    `maven-publish`
    id("com.gradle.plugin-publish")
    id("org.jetbrains.dokka")
}

dependencies {
    implementation("io.gitlab.arturbosch.detekt:detekt-cli:$version")
    implementation(kotlin("stdlib"))
    implementation(kotlin("gradle-plugin"))
    implementation(kotlin("gradle-plugin-api"))
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$version")
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
    tags = listOf("kotlin", "static-code-analysis", "linter", "code-smells")

    (plugins) {
        "detektPlugin" {
            id = "io.gitlab.arturbosch.detekt"
            displayName = "Static code analysis for Kotlin"
        }
    }
}

tasks.dokka {
    outputFormat = "javadoc"
    outputDirectory = "$buildDir/javadoc"
    configuration {
        // suppresses undocumented classes but not dokka warnings
        // https://github.com/Kotlin/dokka/issues/229 && https://github.com/Kotlin/dokka/issues/319
        reportUndocumented = false
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

            internal const val DEFAULT_DETEKT_VERSION = "${rootProject.version}"

            """.trimIndent()
        )
    }
}

sourceSets.main.get().java.srcDir("$buildDir/generated/src")

tasks.compileKotlin {
    dependsOn(generateDefaultDetektVersionFile)
}
