plugins {
    id("module")
}

dependencies {
    api(projects.detektApi)
    api(libs.ksp.symbolProcessingAa)
    testImplementation(projects.detektTestUtils)
    testImplementation(libs.assertj.core)
}
