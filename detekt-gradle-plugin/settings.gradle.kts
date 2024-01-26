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
    id("com.gradle.enterprise") version "3.16.2"
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

val isCiBuild = providers.environmentVariable("CI").isPresent

buildCache {
    local {
        isEnabled = true
    }
    remote<HttpBuildCache> {
        isPush = isCiBuild
        isEnabled = true
        url = uri("https://ge.detekt.dev/cache/")
        credentials {
            username = providers.environmentVariable("GRADLE_CACHE_USERNAME").orNull
            password = providers.environmentVariable("GRADLE_CACHE_PASSWORD").orNull
        }
    }
}
