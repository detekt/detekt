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
    testRuntimeOnly(projects.detektFormatting)
    testImplementation(projects.detektTest)
    testImplementation(testFixtures(projects.detektApi))
    testImplementation(libs.mockk)

    constraints {
        testImplementation("net.bytebuddy:byte-buddy:1.11.6") {
            because("version 1.10.14 (pulled in by mockk 1.11.0) is not Java 16 compatible")
        }
    }
}
