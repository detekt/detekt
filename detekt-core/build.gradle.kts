plugins {
    id("module")
}

dependencies {
    api(projects.detektApi)
    api(projects.detektTooling)
    api(libs.kotlin.compiler)
    implementation(projects.detektKotlinAnalysisApiStandalone)
    implementation(libs.snakeyaml.engine)
    implementation(libs.kotlin.reflect)
    implementation(projects.detektMetrics)
    implementation(projects.detektParser)
    implementation(projects.detektPsiUtils)
    implementation(projects.detektUtils)

    testRuntimeOnly(projects.detektRules)
    testImplementation(projects.detektReportHtml)
    testImplementation(projects.detektReportMd)
    testImplementation(projects.detektReportXml)
    testImplementation(projects.detektReportSarif)
    testImplementation(projects.detektTest)
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
