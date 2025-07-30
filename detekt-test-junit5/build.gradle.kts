plugins {
    id("module")
    id("public-api")
}

dependencies {
    compileOnly(libs.junit.jupiterApi)
    implementation(projects.detektTestUtils)
    implementation(projects.detektKotlinAnalysisApiStandalone)
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.compiler)
    implementation(libs.kotlin.mainKts) {
        isTransitive = false
    }
    implementation(libs.kotlinx.coroutinesCore)
}
