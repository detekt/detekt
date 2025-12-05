plugins {
    id("module")
    id("public-api")
}

dependencies {
    compileOnly(libs.junit.jupiterApi)
    implementation(projects.detektTest)
    implementation(libs.kotlin.scriptingJvm)
}
