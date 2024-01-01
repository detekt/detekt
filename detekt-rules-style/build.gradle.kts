plugins {
    id("module")
}

dependencies {
    compileOnly(projects.detektApi)
    compileOnly(projects.detektMetrics)
    compileOnly(projects.detektTooling)
    testImplementation(projects.detektMetrics)
    testImplementation(projects.detektTest)
    testImplementation(libs.bundles.mocking)
    testImplementation(libs.assertj)
}
