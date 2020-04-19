rootProject.name = "detekt"
include(
    "detekt-api",
    "detekt-core",
    "detekt-rules",
    "detekt-cli",
    "detekt-test",
    "detekt-sample-extensions",
    "detekt-generator",
    "detekt-formatting",
    "detekt-gradle-plugin"
)

pluginManagement {

    repositories {
        gradlePluginPortal()
    }
}

plugins {
    val buildScanVersion: String by settings

    id("com.gradle.enterprise") version buildScanVersion
}
