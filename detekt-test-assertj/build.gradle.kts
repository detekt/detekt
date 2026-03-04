plugins {
    id("module")
    id("public-api")
}

dependencies {
    compileOnly(projects.detektApi)
    api(libs.assertj.coreMinimum)

    testImplementation(libs.kotlin.compiler)
    testImplementation(testFixtures(projects.detektApi))
    testImplementation(projects.detektTestUtils)
    testImplementation(libs.opentest4j)
}
