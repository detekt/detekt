plugins {
    id("module")
    alias(libs.plugins.binaryCompatibilityValidator)
}

dependencies {
    api(projects.detektApi)
    api(projects.detektTestUtils)
    implementation(projects.detektUtils)
    compileOnly(libs.assertj)
    implementation(projects.detektCore)
    implementation(projects.detektParser)
}
