import com.gradle.enterprise.gradleplugin.internal.extension.BuildScanExtensionWithHiddenFeatures

rootProject.name = "detekt"

pluginManagement {
    includeBuild("build-logic")
    includeBuild("build-logic-internal-gradle-detekt-plugin")
}

include("detekt-gradle-plugin")
include("code-coverage-report")
include("detekt-api")
include("detekt-cli")
include("detekt-compiler-plugin")
include("detekt-core")
include("detekt-formatting")
include("detekt-generator")
include("detekt-metrics")
include("detekt-parser")
include("detekt-psi-utils")
include("detekt-report-html")
include("detekt-report-md")
include("detekt-report-sarif")
include("detekt-report-txt")
include("detekt-report-xml")
include("detekt-rules")
include("detekt-rules-complexity")
include("detekt-rules-coroutines")
include("detekt-rules-documentation")
include("detekt-rules-empty")
include("detekt-rules-errorprone")
include("detekt-rules-exceptions")
include("detekt-rules-libraries")
include("detekt-rules-naming")
include("detekt-rules-performance")
include("detekt-rules-ruleauthors")
include("detekt-rules-style")
include("detekt-sample-extensions")
include("detekt-test")
include("detekt-test-utils")
include("detekt-tooling")
include("detekt-utils")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

// build scan plugin can only be applied in settings file
plugins {
    id("com.gradle.enterprise") version "3.13.4"
    id("com.gradle.common-custom-user-data-gradle-plugin") version "1.11.1"
}

val isCiBuild = providers.environmentVariable("CI").isPresent

gradleEnterprise {
    buildScan {
        publishAlways()

        // Publish to scans.gradle.com when `--scan` is used explicitly
        if (!gradle.startParameter.isBuildScan) {
            server = "https://ge.detekt.dev"
            this as BuildScanExtensionWithHiddenFeatures
            publishIfAuthenticated()
        }

        isUploadInBackground = !isCiBuild

        capture {
            isTaskInputFiles = true
        }
    }
}

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

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
