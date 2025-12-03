plugins {
    id("module")
}

dependencies {
    compileOnly(projects.detektApi)
    compileOnly(projects.detektTooling)
    implementation(libs.sarif4k)
    testImplementation(projects.detektTest)
    testImplementation(testFixtures(projects.detektApi))
    testImplementation(libs.assertj.core)
}
