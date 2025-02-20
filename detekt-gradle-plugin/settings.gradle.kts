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
    id("com.gradle.develocity") version "3.19.2"
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

val isCiBuild = providers.environmentVariable("CI").isPresent

// Ensure buildCache config is kept in sync with all builds (root, build-logic & detekt-gradle-plugin)
buildCache {
    local {
        isEnabled = !isCiBuild
    }
    remote(develocity.buildCache) {
        server = "https://ge.detekt.dev"
        isEnabled = false
        isPush = isCiBuild
    }
}
