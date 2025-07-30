plugins {
    id("module")
    id("public-api")
}

dependencies {
    compileOnly(libs.junit.jupiterApi)
    implementation(projects.detektTestUtils)
    implementation(projects.detektKotlinAnalysisApiStandalone)
    implementation(libs.kotlin.compiler)
    implementation(libs.kotlin.scriptingJvm)
    implementation(libs.kotlinx.coroutinesCore)
}
