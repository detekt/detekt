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
    id("com.gradle.develocity") version "4.1"
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("com.autonomousapps.build-health") version "2.19.0"
    // Kotlin plugin must be added to classpath to support build-health analysis
    id("org.jetbrains.kotlin.jvm") version "2.2.10" apply false
}

val isCiBuild = providers.environmentVariable("CI").isPresent

// Ensure buildCache config is kept in sync with all builds (root, build-logic & detekt-gradle-plugin)
buildCache {
    local {
        isEnabled = !isCiBuild
    }
    remote(develocity.buildCache) {
        server = "https://ge.detekt.dev"
        isEnabled = true
        isPush = isCiBuild
    }
}
