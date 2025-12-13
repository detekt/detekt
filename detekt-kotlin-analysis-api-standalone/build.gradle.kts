// This package can be retired once this is closed: https://youtrack.jetbrains.com/issue/KT-56203/AA-Publish-analysis-api-standalone-and-dependencies-to-Maven-Central

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") // Plugin can be removed when KspLibraryModuleBuilder is retired. Blocked by https://youtrack.jetbrains.com/issue/KT-71706
    id("packaging")
    id("com.gradleup.shadow") version "9.3.0"
}

dependencies {
    // Exclude transitive dependencies due to https://youtrack.jetbrains.com/issue/KT-61639
    api(libs.kotlin.analysisApiStandalone) { isTransitive = false }

    // The following dependencies should be removed when KspLibraryModuleBuilder is retired. Blocked by https://youtrack.jetbrains.com/issue/KT-71706
    implementation(libs.kotlin.analysisApi) { isTransitive = false }
    implementation(libs.kotlin.analysisApiImplBase) { isTransitive = false }
    compileOnly(libs.kotlin.compiler)
}

kotlin {
    compilerOptions.jvmTarget = JvmTarget.JVM_1_8
}

java {
    targetCompatibility = JavaVersion.VERSION_1_8
}

val javaComponent = components["java"] as AdhocComponentWithVariants
javaComponent.withVariantsFromConfiguration(configurations["apiElements"]) {
    skip()
}
javaComponent.withVariantsFromConfiguration(configurations["runtimeElements"]) {
    skip()
}
