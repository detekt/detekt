rootProject.name = "detekt"

pluginManagement {
    includeBuild("build-logic")
    includeBuild("detekt-gradle-plugin")
}

include("code-coverage-report")
include("detekt-api")
include("detekt-cli")
include("detekt-compiler-plugin")
include("detekt-core")
include("detekt-generator")
includeBuild("detekt-gradle-plugin")
include("detekt-kotlin-analysis-api")
include("detekt-kotlin-analysis-api-standalone")
include("detekt-metrics")
include("detekt-parser")
include("detekt-psi-utils")
include("detekt-report-html")
include("detekt-report-markdown")
include("detekt-report-sarif")
include("detekt-report-checkstyle")
include("detekt-rules")
include("detekt-rules-complexity")
include("detekt-rules-coroutines")
include("detekt-rules-comments")
include("detekt-rules-empty-blocks")
include("detekt-rules-potential-bugs")
include("detekt-rules-exceptions")
include("detekt-rules-ktlint-wrapper")
include("detekt-rules-ktlint-wrapper:ktlint-repackage")
include("detekt-rules-libraries")
include("detekt-rules-naming")
include("detekt-rules-performance")
include("detekt-rules-ruleauthors")
include("detekt-rules-style")
include("detekt-test")
include("detekt-test-assertj")
include("detekt-test-junit")
include("detekt-test-utils")
include("detekt-tooling")
include("detekt-utils")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

plugins {
    id("com.gradle.develocity") version "4.3"
    id("com.gradle.common-custom-user-data-gradle-plugin") version "2.4.0"
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("com.autonomousapps.build-health") version "3.5.1"
    // Kotlin plugin must be added to classpath to support build-health analysis
    id("org.jetbrains.kotlin.jvm") version "2.3.0" apply false
}

val isCiBuild = providers.environmentVariable("CI").isPresent

develocity {
    buildScan {
        // Publish to scans.gradle.com when `--scan` is used explicitly
        if (!gradle.startParameter.isBuildScan) {
            server = "https://ge.detekt.dev"
            publishing.onlyIf { it.isAuthenticated }
        }

        uploadInBackground = !isCiBuild
    }
}

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

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        exclusiveContent {
            forRepository {
                // Remove when this is closed: https://youtrack.jetbrains.com/issue/KT-56203/AA-Publish-analysis-api-standalone-and-dependencies-to-Maven-Central
                maven("https://redirector.kotlinlang.org/maven/intellij-dependencies")
            }
            filter {
                includeModuleByRegex("org.jetbrains.kotlin", ".*-for-ide")
            }
        }
        exclusiveContent {
            forRepository {
                google()
            }
            filter {
                includeGroupAndSubgroups("com.android")
            }
        }
        mavenCentral()
    }
}
