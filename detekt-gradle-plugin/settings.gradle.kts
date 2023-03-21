rootProject.name = "detekt-gradle-plugin"

pluginManagement {
    includeBuild("../build-logic")
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

plugins {
    id("com.gradle.enterprise") version "3.12.5"
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.4.0"
}
