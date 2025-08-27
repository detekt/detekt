plugins {
    id("module")
}

dependencies {
    compileOnly(projects.detektApi)
    compileOnly(projects.detektMetrics)
    compileOnly(projects.detektPsiUtils)

    testImplementation(libs.kotlin.compiler)
    testImplementation(projects.detektApi)
    testImplementation(projects.detektTest)
    testImplementation(projects.detektTestAssertj)
    testImplementation(projects.detektTestUtils)
    testImplementation(libs.assertj.core)
}
