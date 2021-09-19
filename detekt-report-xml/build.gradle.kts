plugins {
    id("module")
}

dependencies {
    implementation(projects.detektApi)
    testImplementation(testFixtures(projects.detektApi))
    testImplementation(libs.bundles.testImplementation)
    testRuntimeOnly(libs.spek.runner)
}
