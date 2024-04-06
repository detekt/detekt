plugins {
    id("module")
}

dependencies {
    compileOnly(projects.detektApi)
    compileOnly(projects.detektMetrics)
    testImplementation(projects.detektMetrics)
    testImplementation(projects.detektTest)
    testImplementation(libs.assertj)
}
