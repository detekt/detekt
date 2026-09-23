plugins {
    id("module")
}

dependencies {
    api(projects.detektApi)
    testImplementation(projects.detektTestUtils)
    testImplementation(libs.assertj.core)
    api(libs.kotlin.analysisApiSurface)
    implementation(libs.kotlin.analysisApiImplementation)
    api(libs.kotlin.analysisApiIntellijApiSurfaceComponents)
}
