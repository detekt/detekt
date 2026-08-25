plugins {
    id("module")
    id("generator")
}

dependencies {
    compileOnly(projects.detektApi)
    compileOnly(projects.detektMetrics)
    compileOnly(projects.detektRuleHelpers)

    testImplementation(projects.detektApi)
    testRuntimeOnly(projects.detektRuleHelpers)
    testRuntimeOnly(projects.detektMetrics)
    testImplementation(projects.detektTest)
    testImplementation(projects.detektTestAssertj)
    testImplementation(projects.detektTestJunit)
    testImplementation(projects.detektTestUtils)
    testImplementation(libs.assertj.core)
    testCompileOnly(libs.jetbrains.annotations)
}

detektGeneratorConfig.addConfigToResources = false
