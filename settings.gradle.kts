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

// build scan plugin can only be applied in settings file
plugins {
    id("com.gradle.enterprise") version "3.2.1"
}
