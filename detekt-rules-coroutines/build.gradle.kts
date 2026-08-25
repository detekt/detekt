plugins {
    id("module")
    id("generator")
}

dependencies {
    compileOnly(projects.detektApi)
    compileOnly(projects.detektRuleHelpers)

    testImplementation(libs.kotlin.compiler)
    testImplementation(projects.detektKotlinAnalysisApi)
    testImplementation(projects.detektApi)
    testRuntimeOnly(projects.detektRuleHelpers)
    testImplementation(projects.detektTest)
    testImplementation(projects.detektTestAssertj)
    testImplementation(projects.detektTestJunit)
    testImplementation(projects.detektTestUtils)
    testImplementation(libs.assertj.core)

    testRuntimeOnly(libs.kotlinx.coroutinesTest)
    testCompileOnly(libs.jetbrains.annotations)
}

detektGeneratorConfig.addConfigToResources = false
