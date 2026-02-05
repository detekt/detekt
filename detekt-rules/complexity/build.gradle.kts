plugins {
    id("module")
}

dependencies {
    compileOnly(projects.detektApi)
    compileOnly(projects.detektMetrics)
    compileOnly(projects.detektPsiUtils)

    testImplementation(libs.kotlin.compiler)
    testImplementation(projects.detektApi)
    testRuntimeOnly(projects.detektMetrics)
    testRuntimeOnly(projects.detektPsiUtils)
    testImplementation(projects.detektTest)
    testImplementation(projects.detektTestAssertj)
    testImplementation(projects.detektTestJunit)
    testImplementation(projects.detektTestUtils)
    testImplementation(libs.assertj.core)
}
