plugins {
    id("module")
}

dependencies {
    api(libs.kotlin.compiler)
    implementation(projects.detektKotlinAnalysisApiStandalone)
    testImplementation(projects.detektTestUtils)
    testImplementation(libs.assertj.core)
}
