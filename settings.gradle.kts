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
}

gradleEnterprise {
    val isCiBuild = System.getenv("CI") != null

    buildScan {
        server = "https://ge.detekt.dev"
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        if (isCiBuild) {
            termsOfServiceAgree = "yes"
            publishAlways()
        }
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
