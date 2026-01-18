plugins {
    id("module")
    id("public-api")
}

dependencies {
    api(projects.detektApi)
    api(projects.detektTestUtils)
    api(libs.kotlin.compiler)
    implementation(libs.kotlin.reflect)
}
