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

    val dokkaVersion: String by settings

    repositories {
        maven { setUrl("https://plugins.gradle.org/m2/") }
        mavenLocal() // used to publish and test local gradle plugin changes
    }

    plugins {
        id("org.jetbrains.dokka") version dokkaVersion
    }
}

plugins {
    val buildScanVersion: String by settings

    id("com.gradle.enterprise") version buildScanVersion
}
