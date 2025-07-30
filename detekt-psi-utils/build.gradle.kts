plugins {
    id("module")
    id("public-api")
    id("java-test-fixtures")
}

dependencies {
    api(libs.kotlin.compiler)

    implementation(projects.detektKotlinAnalysisApi)

    testImplementation(libs.assertj.core)
    testImplementation(projects.detektTestJunit)
    testImplementation(projects.detektTestUtils)
}

detekt {
    config.from("config/detekt.yml")
}
