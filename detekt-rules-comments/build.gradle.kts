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
    testImplementation(projects.detektTest)
    testImplementation(projects.detektTestAssertj)
    testImplementation(libs.assertj.core)
    testImplementation(projects.detektApi)
    testCompileOnly(libs.jetbrains.annotations)
}

detektGeneratorConfig.addConfigToResources = false
