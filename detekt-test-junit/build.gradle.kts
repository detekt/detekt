plugins {
    id("module")
    id("public-api")
}

dependencies {
    compileOnly(libs.junit.jupiterApi)
    implementation(projects.detektTestUtils)
    implementation(libs.kotlin.scriptingJvm)
}
