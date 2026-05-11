plugins {
    id("module")
    id("public-api")
}

dependencies {
    api(libs.kotlin.compiler)
    compileOnly(projects.detektKotlinAnalysisApi)

    testImplementation(libs.assertj.core)
    testImplementation(projects.detektTestJunit)
    testImplementation(projects.detektTestUtils)
}

detekt {
    config.from("config/detekt.yml")
}
