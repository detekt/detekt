plugins {
    id("module")
}

dependencies {
    api(projects.detektApi)
    api(projects.detektTooling)
    api(libs.kotlin.compiler)
    implementation(projects.detektKotlinAnalysisApi)
    implementation(projects.detektKotlinAnalysisApiStandalone)
    implementation(libs.snakeyaml.engine)
    implementation(libs.kotlin.reflect)
    implementation(projects.detektMetrics)
    implementation(projects.detektParser)
    implementation(projects.detektPsiUtils)
    implementation(projects.detektUtils)
    implementation("androidx.tracing:tracing:2.0.0-alpha04")
    implementation("androidx.tracing:tracing-wire:2.0.0-alpha04")

    testRuntimeOnly(projects.detektRules)
    testImplementation(projects.detektReportHtml)
    testImplementation(projects.detektReportMarkdown)
    testImplementation(projects.detektReportCheckstyle)
    testImplementation(projects.detektReportSarif)
    testImplementation(projects.detektTest)
    testImplementation(projects.detektTestJunit)
    testImplementation(projects.detektTestUtils)
    testImplementation(testFixtures(projects.detektApi))
    testImplementation(libs.classgraph)
    testImplementation(libs.assertj.core)
    testRuntimeOnly(libs.slf4j.simple)
}

consumeGeneratedConfig(
    fromProject = projects.detektGenerator,
    fromConfiguration = "generatedCoreConfig",
    forTask = tasks.sourcesJar
)
