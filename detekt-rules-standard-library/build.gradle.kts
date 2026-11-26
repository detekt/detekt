plugins {
    id("module")
    id("generator")
}

dependencies {
    compileOnly(projects.detektApi)
    compileOnly(projects.detektPsiUtils)
    api(libs.kotlin.analysisApiSurface)

    implementation(libs.kotlin.analysisApiImplementation)
    implementation(libs.kotlin.analysisApiIntellijApiSurfaceComponents)

    testRuntimeOnly(projects.detektPsiUtils)
    testImplementation(projects.detektApi)
    testImplementation(projects.detektTest)
    testImplementation(projects.detektTestAssertj)
    testImplementation(projects.detektTestJunit)
    testImplementation(projects.detektTestUtils)
    testImplementation(libs.assertj.core)
}

detektGeneratorConfig.addConfigToResources = false
