// This package can be retired once this is closed: https://youtrack.jetbrains.com/issue/KT-56203/AA-Publish-analysis-api-standalone-and-dependencies-to-Maven-Central

plugins {
    id("packaging")
    id("com.gradleup.shadow") version "8.3.6"
}

dependencies {
    api(libs.bundles.kotlin.analysisApi) {
        // https://youtrack.jetbrains.com/issue/KT-61639/Standalone-Analysis-API-cannot-find-transitive-dependencies
        isTransitive = false
    }
}

java {
    targetCompatibility = JavaVersion.VERSION_1_8
}

configurations.shadowRuntimeElements {
    attributes {
        // This is not needed in shadow plugin 9+: https://github.com/GradleUp/shadow/pull/1199
        attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, JavaVersion.VERSION_1_8.majorVersion.toInt())
    }
}

val javaComponent = components["java"] as AdhocComponentWithVariants
javaComponent.withVariantsFromConfiguration(configurations["apiElements"]) {
    skip()
}
javaComponent.withVariantsFromConfiguration(configurations["runtimeElements"]) {
    skip()
}
