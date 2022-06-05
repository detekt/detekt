plugins {
    id("module")
}

dependencies {
    implementation(projects.detektMetrics)
    implementation(projects.detektApi)
    implementation(projects.detektUtils)

    testImplementation(testFixtures(projects.detektApi))
    testImplementation(libs.mockk)
    testImplementation(libs.assertj)
}
