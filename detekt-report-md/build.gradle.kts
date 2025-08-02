plugins {
    id("module")
}

dependencies {
    compileOnly(projects.detektApi)
    implementation(projects.detektMetrics)
    implementation(projects.detektUtils)

    testImplementation(testFixtures(projects.detektApi))
    testImplementation(libs.assertj.core)
}
