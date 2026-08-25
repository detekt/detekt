plugins {
    id("module")
    id("generator")
}

dependencies {
    compileOnly(projects.detektApi)
    compileOnly(projects.detektRuleHelpers)

    testImplementation(libs.kotlin.compiler)
    testRuntimeOnly(projects.detektRuleHelpers)
    testImplementation(projects.detektTest)
    testImplementation(projects.detektTestAssertj)
    testImplementation(libs.assertj.core)
    testImplementation(projects.detektApi)
    testCompileOnly(libs.jetbrains.annotations)
}

detektGeneratorConfig.addConfigToResources = false
