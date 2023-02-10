plugins {
    id("module")
}

val generatedCoreConfig: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    api(projects.detektApi)
    api(projects.detektParser)
    api(projects.detektTooling)
    implementation(libs.snakeyaml)
    implementation(libs.kotlin.reflection)
    implementation(projects.detektMetrics)
    implementation(projects.detektPsiUtils)
    implementation(projects.detektReportHtml)
    implementation(projects.detektReportMd)
    implementation(projects.detektReportTxt)
    implementation(projects.detektReportXml)
    implementation(projects.detektReportSarif)
    implementation(projects.detektUtils)

    testRuntimeOnly(projects.detektRules)
    testImplementation(projects.detektTest)
    testImplementation(testFixtures(projects.detektApi))
    testImplementation(libs.mockk)
    testImplementation(libs.reflections)
    testImplementation(libs.assertj)
    testRuntimeOnly(libs.slf4j.simple)

    generatedCoreConfig(
        project(":detekt-generator", "generatedCoreConfig")
    )
}

tasks.named("sourcesJar").configure {
    inputs.files(generatedCoreConfig)
        .withPropertyName(generatedCoreConfig.name)
        .withPathSensitivity(PathSensitivity.RELATIVE)
}

