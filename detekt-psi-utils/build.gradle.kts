plugins {
    id("module")
    id("public-api")
}

dependencies {
    api(libs.kotlin.compiler)

    implementation(projects.detektKotlinAnalysisApi)

    testImplementation(libs.assertj.core)
    testImplementation(projects.detektTestJunit)
    testImplementation(projects.detektTest)
}

detekt {
    config.from("config/detekt.yml")
}
