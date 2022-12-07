import com.gradle.enterprise.gradleplugin.internal.extension.BuildScanExtensionWithHiddenFeatures

rootProject.name = "detekt"

pluginManagement {
    includeBuild("build-logic")
    includeBuild("detekt-gradle-plugin")
}
includeBuild("detekt-gradle-plugin")

include("code-coverage-report")
include("detekt-api")
include("detekt-cli")
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

// build scan plugin can only be applied in settings file
plugins {
    id("com.gradle.enterprise") version "3.11.4"
    id("com.gradle.common-custom-user-data-gradle-plugin") version "1.8.2"
}

val isCiBuild = System.getenv("CI") != null

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
            username = System.getenv("GRADLE_CACHE_USERNAME")
            password = System.getenv("GRADLE_CACHE_PASSWORD")
        }
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
