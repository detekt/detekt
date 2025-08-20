plugins {
    id("module")
}

dependencies {
    runtimeOnly(projects.detektRulesComplexity)
    runtimeOnly(projects.detektRulesCoroutines)
    runtimeOnly(projects.detektRulesDocumentation)
    runtimeOnly(projects.detektRulesEmpty)
    runtimeOnly(projects.detektRulesErrorprone)
    runtimeOnly(projects.detektRulesExceptions)
    runtimeOnly(projects.detektRulesNaming)
    runtimeOnly(projects.detektRulesPerformance)
    runtimeOnly(projects.detektRulesStyle)

    testImplementation(projects.detektTestUtils)
    testImplementation(projects.detektRulesComplexity)
    testImplementation(projects.detektRulesCoroutines)
    testImplementation(projects.detektRulesDocumentation)
    testImplementation(projects.detektRulesEmpty)
    testImplementation(projects.detektRulesErrorprone)
    testImplementation(projects.detektRulesExceptions)
    testImplementation(projects.detektRulesNaming)
    testImplementation(projects.detektRulesPerformance)
    testImplementation(projects.detektRulesStyle)
    testImplementation(libs.assertj.core)
    testImplementation(libs.classgraph)
    testImplementation(testFixtures(projects.detektApi))
}
