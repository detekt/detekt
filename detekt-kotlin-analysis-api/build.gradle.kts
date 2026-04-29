// This package can be retired once this is closed: https://youtrack.jetbrains.com/issue/KT-56203/AA-Publish-analysis-api-standalone-and-dependencies-to-Maven-Central

plugins {
    id("packaging")
    id("com.gradleup.shadow") version "9.4.1"
}

val aaDependency = configurations.dependencyScope("aaDependency")
val aaDependencies = configurations.resolvable("aaDependencies") {
    extendsFrom(aaDependency.get())
}

dependencies {
    // Exclude transitive dependencies due to https://youtrack.jetbrains.com/issue/KT-61639
    aaDependency(libs.kotlin.analysisApi) { isTransitive = false }
    aaDependency(libs.kotlin.analysisApiK2) { isTransitive = false }

    aaDependency(libs.kotlin.analysisApiImplBase) { isTransitive = false }
    aaDependency(libs.kotlin.analysisApiPlatformInterface) { isTransitive = false }
    aaDependency(libs.kotlin.lowLevelApiFir) { isTransitive = false }
    aaDependency(libs.kotlin.symbolLightClasses) { isTransitive = false }
    runtimeOnly(libs.caffeine) {
        attributes {
            // https://github.com/ben-manes/caffeine/issues/716
            // Remove on upgrade to Caffeine 3.x or if https://youtrack.jetbrains.com/issue/KT-73751 is fixed
            attribute(Bundling.BUNDLING_ATTRIBUTE, named(Bundling.EXTERNAL))
        }
    }
    runtimeOnly(libs.kotlinx.serializationCore)
    runtimeOnly(libs.kotlinx.coroutinesCore.intellij)
}

tasks.shadowJar {
    configurations = aaDependencies.map { listOf(it) }
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
