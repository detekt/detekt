plugins {
    id("module")
    id("generator")
}

dependencies {
    compileOnly(projects.detektApi)
    compileOnly(projects.detektRuleHelpers)

    testImplementation(projects.detektApi)
    testRuntimeOnly(projects.detektRuleHelpers)
    testImplementation(projects.detektTest)
    testImplementation(projects.detektTestUtils)
    testImplementation(projects.detektTestAssertj)
    testImplementation(projects.detektTestJunit)
    testImplementation(libs.assertj.core)
}
