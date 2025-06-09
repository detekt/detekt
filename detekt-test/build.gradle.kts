plugins {
    id("module")
    id("public-api")
}

dependencies {
    api(projects.detektApi)
    api(projects.detektTestUtils)
    implementation(projects.detektKotlinAnalysisApiStandalone)
    implementation(projects.detektUtils)
    implementation(libs.kotlin.reflect)
    compileOnly(libs.assertj.core)
    implementation(projects.detektCore)
}
