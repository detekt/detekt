import com.gradle.enterprise.gradleplugin.internal.extension.BuildScanExtensionWithHiddenFeatures

rootProject.name = "detekt"

pluginManagement {
    includeBuild("build-logic")
}

include("code-coverage-report")
include("custom-checks")
include("detekt-api")
include("detekt-cli")
include("detekt-core")
include("detekt-formatting")
include("detekt-generator")
include("detekt-gradle-plugin")
include("detekt-metrics")
include("detekt-parser")
include("detekt-psi-utils")
include("detekt-report-html")
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
include("detekt-rules-naming")
include("detekt-rules-performance")
include("detekt-rules-style")
include("detekt-sample-extensions")
include("detekt-test")
include("detekt-test-utils")
include("detekt-tooling")
include("detekt-utils")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// build scan plugin can only be applied in settings file
plugins {
    `gradle-enterprise`
    id("com.gradle.common-custom-user-data-gradle-plugin") version "1.6.5"
}

gradleEnterprise {
    val isCiBuild = System.getenv("CI") != null

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

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
