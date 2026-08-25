plugins {
    id("module")
    id("generator")
}

dependencies {
    compileOnly(projects.detektApi)
    compileOnly(projects.detektRuleHelpers)

    testImplementation(libs.kotlin.compiler)
    testRuntimeOnly(projects.detektRuleHelpers)
    testImplementation(projects.detektApi)
    testImplementation(projects.detektTest)
    testImplementation(projects.detektTestAssertj)
    testImplementation(projects.detektTestJunit)
    testImplementation(projects.detektTestUtils)
    testImplementation(libs.assertj.core)
}

detektGeneratorConfig.addConfigToResources = false
