plugins {
    id("module")
    id("generator")
}

dependencies {
    compileOnly(projects.detektApi)

    testImplementation(libs.ksp.symbolProcessingAa)
    testImplementation(projects.detektApi)
    testImplementation(projects.detektTest)
    testImplementation(projects.detektTestAssertj)
    testImplementation(projects.detektTestJunit)
    testImplementation(projects.detektTestUtils)
    testImplementation(libs.assertj.core)
}

detektGeneratorConfig.addConfigToResources = false
