plugins {
    module
}

dependencies {
    compileOnly(projects.detektApi)
    compileOnly(projects.detektTooling)
    implementation(libs.sarif4k)
    testImplementation(projects.detektTooling)
    testImplementation(projects.detektTestUtils)
    testImplementation(testFixtures(projects.detektApi))
}
