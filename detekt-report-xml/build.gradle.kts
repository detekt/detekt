plugins {
    id("module")
}

dependencies {
    compileOnly(projects.detektApi)
    testImplementation(testFixtures(projects.detektApi))
    testImplementation(libs.assertj.core)
}
