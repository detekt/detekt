plugins {
    module
}

dependencies {
    implementation(libs.snakeyaml)
    implementation(projects.detektApi)
    implementation(projects.detektMetrics)
    implementation(projects.detektParser)
    implementation(projects.detektPsiUtils)
    implementation(projects.detektTooling)
    implementation(projects.detektReportHtml)
    implementation(projects.detektReportTxt)
    implementation(projects.detektReportXml)
    implementation(projects.detektReportSarif)

    testRuntimeOnly(projects.detektRules)
    testImplementation(projects.detektTest)
    testImplementation(testFixtures(projects.detektApi))
}
