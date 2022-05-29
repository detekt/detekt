plugins {
    id("module")
}

dependencies {
    implementation(projects.detektMetrics)
    implementation(projects.detektApi)
    implementation(projects.detektTooling)

    testImplementation(testFixtures(projects.detektApi))
    testImplementation(libs.mockk)
    testImplementation(libs.assertj)
}
