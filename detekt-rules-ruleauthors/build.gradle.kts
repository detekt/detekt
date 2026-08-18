plugins {
    id("module")
    id("generator")
}

dependencies {
    compileOnly(projects.detektApi)
    compileOnly(projects.detektPsiUtils)

    testImplementation(libs.ksp.symbolProcessingAa)
    testImplementation(projects.detektApi)
    testRuntimeOnly(projects.detektPsiUtils)
    testImplementation(projects.detektTest)
    testImplementation(projects.detektTestUtils)
    testImplementation(projects.detektTestAssertj)
    testImplementation(projects.detektTestJunit)
    testImplementation(libs.assertj.core)
}
