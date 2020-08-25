rootProject.name = "detekt"
include(
    "custom-checks",
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
    "detekt-rules-complexity",
    "detekt-rules-coroutines",
    "detekt-rules-documentation",
    "detekt-rules-empty",
    "detekt-rules-errorprone",
    "detekt-rules-exceptions",
    "detekt-rules-naming",
    "detekt-rules-performance",
    "detekt-rules-style",
    "detekt-sample-extensions",
    "detekt-test",
    "detekt-test-utils",
    "detekt-tooling"
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
