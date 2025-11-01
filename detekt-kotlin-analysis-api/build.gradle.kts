// This package can be retired once this is closed: https://youtrack.jetbrains.com/issue/KT-56203/AA-Publish-analysis-api-standalone-and-dependencies-to-Maven-Central

plugins {
    id("packaging")
    id("com.gradleup.shadow") version "9.2.2"
}

dependencies {
    // Exclude transitive dependencies due to https://youtrack.jetbrains.com/issue/KT-61639
    api(libs.kotlin.analysisApi) { isTransitive = false }
    api(libs.kotlin.analysisApiK2) { isTransitive = false }

    implementation(libs.kotlin.analysisApiImplBase) { isTransitive = false }
    implementation(libs.kotlin.analysisApiPlatformInterface) { isTransitive = false }
    implementation(libs.kotlin.lowLevelApiFir) { isTransitive = false }
    implementation(libs.kotlin.symbolLightClasses) { isTransitive = false }
    implementation(libs.caffeine) {
        attributes {
            // https://github.com/ben-manes/caffeine/issues/716
            // Remove on upgrade to Caffeine 3.x or if https://youtrack.jetbrains.com/issue/KT-73751 is fixed
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
        }
    }
    implementation(libs.kotlinx.serializationCore)
}

java {
    targetCompatibility = JavaVersion.VERSION_1_8
}

// https://gradleup.com/shadow/publishing/#publishing-the-shadowed-gradle-plugins
tasks.shadowJar {
    archiveClassifier = ""
}

val javaComponent = components["java"] as AdhocComponentWithVariants
javaComponent.withVariantsFromConfiguration(configurations["apiElements"]) {
    skip()
}
javaComponent.withVariantsFromConfiguration(configurations["runtimeElements"]) {
    skip()
}
