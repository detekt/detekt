rootProject.name = "detekt"
include("detekt-api",
        "detekt-core",
        "detekt-rules",
        "detekt-cli",
        "detekt-test",
        "detekt-sample-extensions",
        "detekt-generator",
        "detekt-formatting")

includeBuild("detekt-gradle-plugin")

val buildScanVersion: String by settings
val kotlinVersion: String by settings
val bintrayVersion: String by settings
val artifactoryVersion: String by settings
val gradleVersionsPluginVersion: String by settings
val shadowVersion: String by settings
val sonarQubeVersion: String by settings
val dokkaVersion: String by settings

pluginManagement {
        plugins {
                id("com.gradle.build-scan") version buildScanVersion
                kotlin("jvm") version kotlinVersion
                id("com.jfrog.bintray") version bintrayVersion
                id("com.jfrog.artifactory") version artifactoryVersion
                id("com.github.ben-manes.versions") version gradleVersionsPluginVersion
                id("com.github.johnrengelman.shadow") version shadowVersion
                id("org.sonarqube") version sonarQubeVersion
                id("org.jetbrains.dokka") version dokkaVersion
        }
}
