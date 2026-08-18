plugins {
    id("module")
    id("public-api")
}

dependencies {
    api(libs.ksp.symbolProcessingAa)

    testImplementation(libs.assertj.core)
    testImplementation(projects.detektTestJunit)
    testImplementation(projects.detektTestUtils)
    testCompileOnly(libs.jetbrains.annotations)
}

detekt {
    config.from("config/detekt.yml")
}
