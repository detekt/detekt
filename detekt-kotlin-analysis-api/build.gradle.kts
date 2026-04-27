// This package can be retired once this is closed: https://youtrack.jetbrains.com/issue/KT-56203/AA-Publish-analysis-api-standalone-and-dependencies-to-Maven-Central

plugins {
    id("packaging")
    id("com.gradleup.shadow") version "9.4.1"
}

dependencies {
    // Exclude transitive dependencies due to https://youtrack.jetbrains.com/issue/KT-61639
    api(libs.kotlin.analysisApi) { isTransitive = false }
    api(libs.kotlin.analysisApiK2) { isTransitive = false }

    implementation(libs.kotlin.analysisApiImplBase) { isTransitive = false }
    implementation(libs.kotlin.analysisApiPlatformInterface) { isTransitive = false }
    implementation(libs.kotlin.lowLevelApiFir) { isTransitive = false }
    implementation(libs.kotlin.symbolLightClasses) { isTransitive = false }
    shadow(libs.caffeine) {
        attributes {
            // https://github.com/ben-manes/caffeine/issues/716
            // Remove on upgrade to Caffeine 3.x or if https://youtrack.jetbrains.com/issue/KT-73751 is fixed
            attribute(Bundling.BUNDLING_ATTRIBUTE, named(Bundling.EXTERNAL))
        }
    }
    shadow(libs.kotlinx.serializationCore)
    shadow(libs.kotlinx.coroutinesCore.intellij)
}

val sourcesJar = tasks.register<Jar>("sourcesJar") {
    archiveClassifier = "sources"

    from(
        configurations.runtimeClasspath.map {
            it.incoming.artifactView {
                withVariantReselection()
                attributes {
                    attribute(Category.CATEGORY_ATTRIBUTE, named(Category.DOCUMENTATION))
                    attribute(DocsType.DOCS_TYPE_ATTRIBUTE, named(DocsType.SOURCES))
                }
            }.files.map { jar -> zipTree(jar) }
        }
    )
}

java {
    targetCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
}

val javaComponent = components["java"] as AdhocComponentWithVariants
javaComponent.withVariantsFromConfiguration(configurations["apiElements"]) {
    skip()
}
javaComponent.withVariantsFromConfiguration(configurations["runtimeElements"]) {
    skip()
}
