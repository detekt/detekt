plugins {
    id("module")
    id("generator")
}

dependencies {
    compileOnly(projects.detektApi)
    compileOnly(projects.detektPsiUtils)
    api(libs.kotlin.analysisApiSurface)

    implementation(libs.kotlin.analysisApiFirDiagnostics)
    implementation(libs.kotlin.analysisApiImplementation)
    api(libs.kotlin.analysisApiIntellijApiSurfaceComponents)

    testImplementation(projects.detektApi)
    testRuntimeOnly(projects.detektPsiUtils)
    testImplementation(projects.detektTest)
    testImplementation(projects.detektTestAssertj)
    testImplementation(projects.detektTestJunit)
    testImplementation(projects.detektTestUtils)
    testImplementation(libs.assertj.core)
}

detektGeneratorConfig.addConfigToResources = false
