plugins {
    id("module")
    id("public-api")
}

dependencies {
    compileOnly(projects.detektApi)
    api(libs.assertj.coreMinimum)

    testImplementation(testFixtures(projects.detektApi))
    testImplementation(projects.detektTestUtils)
    testImplementation(libs.assertj.core)
    testImplementation(libs.opentest4j)
}
