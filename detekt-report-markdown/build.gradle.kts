plugins {
    id("module")
}

dependencies {
    compileOnly(projects.detektApi)
    compileOnly(projects.detektReportComplexity)
    implementation(projects.detektUtils)

    testImplementation(testFixtures(projects.detektApi))
    testImplementation(libs.assertj.core)
    testImplementation(projects.detektReportComplexity)
    testImplementation(libs.kotlin.compiler)
}
