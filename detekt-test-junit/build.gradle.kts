plugins {
    id("module")
    id("public-api")
}

dependencies {
    api(libs.junit.jupiterApi)
    implementation(projects.detektTestUtils)
}
