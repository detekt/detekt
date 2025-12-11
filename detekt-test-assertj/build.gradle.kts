plugins {
    id("module")
    id("public-api")
}

dependencies {
    compileOnly(projects.detektApi)
    compileOnly(libs.assertj.core)

    testImplementation(testFixtures(projects.detektApi))
    testImplementation(projects.detektTest)
    testImplementation(libs.assertj.core)
    testImplementation(libs.opentest4j)
}
