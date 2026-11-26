plugins {
    id("module")
}

dependencies {
    api(projects.detektApi)
    api(projects.detektTooling)
    implementation(projects.detektPsiUtils)
    implementation(libs.kotlin.analysisApiIntellijApiSurfaceComponents)
    implementation(libs.kotlin.analysisApiSurface)

    testImplementation(projects.detektTest)
    testImplementation(projects.detektTestJunit)
    testImplementation(projects.detektTestUtils)
    testImplementation(libs.assertj.core)
}
