rootProject.name = "detekt"
include(
    "detekt-api",
    "detekt-cli",
    "detekt-bom",
    "detekt-core",
    "detekt-formatting",
    "detekt-generator",
    "detekt-gradle-plugin",
    "detekt-metrics",
    "detekt-parser",
    "detekt-psi-utils",
    "detekt-report-html",
    "detekt-report-txt",
    "detekt-report-xml",
    "detekt-rules",
    "detekt-sample-extensions",
    "detekt-test",
    "detekt-test-utils"
)

pluginManagement {

    repositories {
        gradlePluginPortal()
    }
}

// build scan plugin can only be applied in settings file
plugins {
    id("com.gradle.enterprise") version "3.3.1"
}
