plugins {
    id("module")
    id("generator")
}

dependencies {
    compileOnly(projects.detektApi)
    compileOnly(projects.detektPsiUtils)

    api(libs.kotlin.analysisApiSurface)
    implementation(libs.kotlin.analysisApiIntellijApiSurfaceComponents)

    implementation(libs.kotlin.analysisApiImplementation)

    testImplementation(projects.detektApi)
    testRuntimeOnly(projects.detektPsiUtils)
    testImplementation(projects.detektTest)
    testImplementation(projects.detektTestAssertj)
    testImplementation(projects.detektTestJunit)
    testImplementation(projects.detektTestUtils)
    testImplementation(libs.assertj.core)
}

detektGeneratorConfig.addConfigToResources = false
