plugins {
    id("module")
}

dependencies {
    compileOnly(projects.detektApi)
    implementation(libs.sarif4k)
    testImplementation(projects.detektTestUtils)
    testImplementation(testFixtures(projects.detektApi))
    testImplementation(libs.assertj.core)
}
