plugins {
    id("module")
}

dependencies {
    compileOnly(projects.detektApi)
    implementation(projects.detektMetrics)
    implementation(projects.detektUtils)

    testImplementation(projects.detektTest)
    testImplementation(testFixtures(projects.detektApi))
    testImplementation(libs.assertj.core)
    testImplementation(libs.kotlin.compiler)
}
