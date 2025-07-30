plugins {
    id("module")
    id("public-api")
}

dependencies {
    api(projects.detektApi)
    api(projects.detektTestAssertj)
    api(projects.detektTestUtils)
    api(libs.kotlin.compiler)
    implementation(projects.detektKotlinAnalysisApiStandalone)
    implementation(projects.detektUtils)
    implementation(libs.kotlin.reflect)
    implementation(projects.detektCore)
}
