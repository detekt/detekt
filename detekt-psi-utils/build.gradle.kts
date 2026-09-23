plugins {
    id("module")
    id("public-api")
}

dependencies {
    implementation(libs.kotlin.analysisApiFirDiagnostics)
    implementation(libs.kotlin.analysisApiImplementation)
    api(libs.kotlin.analysisApiIntellijApiSurfaceComponents)
    api(libs.kotlin.analysisApiSurface)

    testImplementation(libs.assertj.core)
    testImplementation(projects.detektTestJunit)
    testImplementation(projects.detektTestUtils)
    testCompileOnly(libs.jetbrains.annotations)
}

detekt {
    config.from("config/detekt.yml")
}
