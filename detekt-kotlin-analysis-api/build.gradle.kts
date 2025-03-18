// This package can be retired once this is closed: https://youtrack.jetbrains.com/issue/KT-56203/AA-Publish-analysis-api-standalone-and-dependencies-to-Maven-Central

plugins {
    id("packaging")
    id("com.gradleup.shadow") version "8.3.6"
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-ide-plugin-dependencies")
}

dependencies {
    implementation(libs.bundles.kotlin.analysisApi) {
        // https://youtrack.jetbrains.com/issue/KT-61639/Standalone-Analysis-API-cannot-find-transitive-dependencies
        isTransitive = false
    }
}

tasks.shadowJar {
    archiveClassifier = ""
}
