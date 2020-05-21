rootProject.name = "detekt"
include(
    "detekt-api",
    "detekt-cli",
    "detekt-core",
    "detekt-formatting",
    "detekt-generator",
    "detekt-gradle-plugin",
    "detekt-metrics",
    "detekt-parser",
    "detekt-rules",
    "detekt-sample-extensions",
    "detekt-test"
)

pluginManagement {

    repositories {
        gradlePluginPortal()
    }
}

// build scan plugin can only be applied in settings file
plugins {
    id("com.gradle.enterprise") version "3.2.1"
}
