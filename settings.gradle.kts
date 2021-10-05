rootProject.name = "detekt"

pluginManagement {
    includeBuild("build-logic")
}

include(
    "code-coverage-report",
    "custom-checks",
    "detekt-api",
    "detekt-cli",
    "detekt-core",
    "detekt-formatting",
    "detekt-generator",
    "detekt-gradle-plugin",
    "detekt-metrics",
    "detekt-parser",
    "detekt-psi-utils",
    "detekt-report-html",
    "detekt-report-sarif",
    "detekt-report-txt",
    "detekt-report-xml",
    "detekt-rules",
    "detekt-rules-complexity",
    "detekt-rules-coroutines",
    "detekt-rules-documentation",
    "detekt-rules-empty",
    "detekt-rules-errorprone",
    "detekt-rules-exceptions",
    "detekt-rules-naming",
    "detekt-rules-performance",
    "detekt-rules-style",
    "detekt-sample-extensions",
    "detekt-test",
    "detekt-test-utils",
    "detekt-tooling"
)

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// build scan plugin can only be applied in settings file
plugins {
    id("com.gradle.enterprise") version "3.6.4"
}

gradleEnterprise {
    val isCiBuild = System.getenv("CI") != null

    buildScan {
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
