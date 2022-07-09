plugins {
    id("module")
}

dependencies {
    api(projects.detektApi)
    api(projects.detektParser)
    api(projects.detektTooling)
    implementation(libs.snakeyaml)
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
}
