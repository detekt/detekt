plugins {
    id("module")
}

dependencies {
    api(libs.kotlin.compiler)
    implementation(projects.detektKotlinAnalysisApiStandalone)
    testImplementation(libs.assertj.core)
}
