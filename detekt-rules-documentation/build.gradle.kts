plugins {
    id("module")
}

dependencies {
    compileOnly(projects.detektApi)
    compileOnly(projects.detektPsiUtils)
    compileOnly(libs.jetbrains.annotations)

    testImplementation(libs.kotlin.compiler)
    testImplementation(projects.detektTest)
    testImplementation(projects.detektTestAssertj)
    testImplementation(libs.assertj.core)
    testImplementation(testFixtures(projects.detektApi))
}
