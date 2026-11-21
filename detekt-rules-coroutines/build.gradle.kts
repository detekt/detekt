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

    testImplementation(projects.detektApi)
    testRuntimeOnly(projects.detektPsiUtils)
    testImplementation(projects.detektTest)
    testImplementation(projects.detektTestAssertj)
    testImplementation(projects.detektTestJunit)
    testImplementation(projects.detektTestUtils)
    testImplementation(libs.assertj.core)

    testRuntimeOnly(libs.kotlinx.coroutinesTest)
    testCompileOnly(libs.jetbrains.annotations)
}

detektGeneratorConfig.addConfigToResources = false
