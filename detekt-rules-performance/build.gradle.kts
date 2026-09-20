plugins {
    id("module")
    id("generator")
}

dependencies {
    api(libs.kotlin.analysisApiSurface)
    compileOnly(projects.detektApi)
    implementation(libs.kotlin.analysisApiImplementation)
    implementation(libs.kotlin.analysisApiIntellijApiSurfaceComponents)

    testImplementation(projects.detektApi)
    testImplementation(projects.detektTest)
    testImplementation(projects.detektTestAssertj)
    testImplementation(projects.detektTestJunit)
    testImplementation(projects.detektTestUtils)
    testImplementation(libs.assertj.core)
}

detektGeneratorConfig.addConfigToResources = false
