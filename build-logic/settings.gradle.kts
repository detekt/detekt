rootProject.name = "build-logic"

plugins {
    id("com.gradle.develocity") version "3.17"
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

val isCiBuild = providers.environmentVariable("CI").isPresent

buildCache {
    local {
        isEnabled = !isCiBuild
    }
    remote(develocity.buildCache) {
        server = "https://ge.detekt.dev"
        isEnabled = true
        val accessKey = System.getenv("DEVELOCITY_ACCESS_KEY")
        isPush = isCiBuild && !accessKey.isNullOrEmpty()
    }
}
