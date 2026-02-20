plugins {
    id("module")
}

dependencies {
    runtimeOnly(projects.detektRulesComplexity)
    runtimeOnly(projects.detektRulesCoroutines)
    runtimeOnly(projects.detektRulesComments)
    runtimeOnly(projects.detektRulesEmptyBlocks)
    runtimeOnly(projects.detektRulesPotentialBugs)
    runtimeOnly(projects.detektRulesExceptions)
    runtimeOnly(projects.detektRulesNaming)
    runtimeOnly(projects.detektRulesPerformance)
    runtimeOnly(projects.detektRulesStyle)

    testImplementation(projects.detektRulesComplexity)
    testImplementation(projects.detektRulesCoroutines)
    testImplementation(projects.detektRulesComments)
    testImplementation(projects.detektRulesEmptyBlocks)
    testImplementation(projects.detektRulesPotentialBugs)
    testImplementation(projects.detektRulesExceptions)
    testImplementation(projects.detektRulesNaming)
    testImplementation(projects.detektRulesPerformance)
    testImplementation(projects.detektRulesStyle)
    testImplementation(libs.assertj.core)
    testImplementation(libs.classgraph)
    testImplementation(projects.detektApi)
}
