plugins {
    id("module")
}

dependencies {
    compileOnly(projects.detektApi)
    compileOnly(projects.detektPsiUtils)
    compileOnly(projects.detektMetrics)

    testRuntimeOnly(projects.detektPsiUtils)
    testRuntimeOnly(projects.detektApi)
    testRuntimeOnly(projects.detektMetrics)
    testImplementation(projects.detektTest)
    testImplementation(projects.detektTestUtils)
    testImplementation(testFixtures(projects.detektApi))
    testImplementation(libs.assertj.core)
    testImplementation(libs.kotlin.compiler)
}
