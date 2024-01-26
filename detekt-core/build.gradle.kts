plugins {
    id("module")
}

dependencies {
    api(projects.detektApi)
    api(projects.detektParser)
    api(projects.detektTooling)
    implementation(libs.snakeyaml)
    implementation(libs.kotlin.reflection)
    implementation(projects.detektMetrics)
    implementation(projects.detektPsiUtils)
    implementation(projects.detektUtils)

    testRuntimeOnly(projects.detektRules)
    testImplementation(projects.detektReportHtml)
    testImplementation(projects.detektReportMd)
    testImplementation(projects.detektReportSarif)
    testImplementation(projects.detektReportTxt)
    testImplementation(projects.detektReportXml)
    testImplementation(projects.detektTest)
    testImplementation(testFixtures(projects.detektApi))
    testImplementation(libs.classgraph)
    testImplementation(libs.assertj)
    testRuntimeOnly(libs.slf4j.simple)
}

consumeGeneratedConfig(
    fromProject = projects.detektGenerator,
    fromConfiguration = "generatedCoreConfig",
    forTask = tasks.sourcesJar
)
