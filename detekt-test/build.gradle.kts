plugins {
    id("module")
    id("public-api")
}

dependencies {
    api(projects.detektApi)
    api(projects.detektTestUtils)
    implementation(projects.detektUtils)
    compileOnly(libs.assertj)
    implementation(projects.detektCore)
}
