plugins {
    id("module")
}

dependencies {
    compileOnly(projects.detektApi)

    testImplementation(projects.detektTest)
    testImplementation(testFixtures(projects.detektApi))
    testImplementation(libs.assertj.core)
}
