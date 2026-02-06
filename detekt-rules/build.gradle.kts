plugins {
    id("module")
}

dependencies {
    runtimeOnly(projects.detektRules.complexity)
    runtimeOnly(projects.detektRules.coroutines)
    runtimeOnly(projects.detektRules.comments)
    runtimeOnly(projects.detektRules.emptyBlocks)
    runtimeOnly(projects.detektRules.potentialBugs)
    runtimeOnly(projects.detektRules.exceptions)
    runtimeOnly(projects.detektRules.naming)
    runtimeOnly(projects.detektRules.performance)
    runtimeOnly(projects.detektRules.style)

    testImplementation(projects.detektTestUtils)
    testImplementation(projects.detektRules.complexity)
    testImplementation(projects.detektRules.coroutines)
    testImplementation(projects.detektRules.comments)
    testImplementation(projects.detektRules.emptyBlocks)
    testImplementation(projects.detektRules.potentialBugs)
    testImplementation(projects.detektRules.exceptions)
    testImplementation(projects.detektRules.naming)
    testImplementation(projects.detektRules.performance)
    testImplementation(projects.detektRules.style)
    testImplementation(libs.assertj.core)
    testImplementation(libs.classgraph)
    testImplementation(testFixtures(projects.detektApi))
}
