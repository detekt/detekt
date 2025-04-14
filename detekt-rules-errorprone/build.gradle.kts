plugins {
    id("module")
}

dependencies {
    compileOnly(projects.detektApi)
    compileOnly(projects.detektKotlinAnalysisApi)
    compileOnly(projects.detektPsiUtils)
    implementation(projects.detektTooling)
    testImplementation(projects.detektTest)
    testImplementation(libs.assertj.core)
}
