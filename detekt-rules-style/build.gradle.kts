plugins {
    id("module")
    id("generator")
}

dependencies {
    compileOnly(projects.detektApi)
    compileOnly(projects.detektMetrics)
    compileOnly(projects.detektPsiUtils)
    implementation(libs.kotlin.analysisApiImplementation)
    api(libs.kotlin.analysisApiIntellijApiSurfaceComponents)
    api(libs.kotlin.analysisApiSurface)

    testImplementation(projects.detektApi)
    testRuntimeOnly(projects.detektPsiUtils)
    testRuntimeOnly(projects.detektMetrics)
    testImplementation(projects.detektTest)
    testImplementation(projects.detektTestAssertj)
    testImplementation(projects.detektTestJunit)
    testImplementation(projects.detektTestUtils)
    testImplementation(libs.assertj.core)
    testCompileOnly(libs.jetbrains.annotations)
}

detektGeneratorConfig.addConfigToResources = false
