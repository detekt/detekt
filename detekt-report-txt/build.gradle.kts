plugins {
    id("module")
}

dependencies {
    implementation(projects.detektApi)
    testImplementation(testFixtures(projects.detektApi))
    testImplementation(libs.assertj)
}
