plugins {
    id("module")
}

dependencies {
    api(projects.detektApi)
    api(libs.kotlin.compiler)
    compileOnly(projects.detektPsiUtils)
    implementation(projects.detektMetrics)
    testImplementation(projects.detektTest)
    testImplementation(projects.detektTestUtils)
    testImplementation(testFixtures(projects.detektApi))
    testImplementation(libs.assertj.core)
    testRuntimeOnly(projects.detektPsiUtils)
}
