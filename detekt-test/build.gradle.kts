plugins {
    id("module")
    id("public-api")
}

dependencies {
    api(projects.detektApi)
    api(projects.detektTestUtils)
    api(libs.kotlin.compiler)
    implementation(projects.detektUtils)
    implementation(libs.kotlin.reflect)
    implementation(projects.detektCore)
}
