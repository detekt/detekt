// This package can be retired once this is closed: https://youtrack.jetbrains.com/issue/KT-56203/AA-Publish-analysis-api-standalone-and-dependencies-to-Maven-Central

plugins {
    id("packaging")
    id("com.gradleup.shadow") version "8.3.6"
}

dependencies {
    // Exclude transitive dependencies due to https://youtrack.jetbrains.com/issue/KT-61639
    implementation(libs.kotlin.analysisApi) { isTransitive = false }
    implementation(libs.kotlin.analysisApiStandalone) { isTransitive = false }
    implementation(libs.kotlin.analysisApiImplBase) { isTransitive = false }
    implementation(libs.kotlin.analysisApiK2) { isTransitive = false }
    implementation(libs.kotlin.analysisApiPlatformInterface) { isTransitive = false }
    implementation(libs.kotlin.lowLevelApiFir) { isTransitive = false }
    implementation(libs.kotlin.symbolLightClasses) { isTransitive = false }
    implementation(libs.caffeine)
    }
}

tasks.shadowJar {
    archiveClassifier = ""
}
