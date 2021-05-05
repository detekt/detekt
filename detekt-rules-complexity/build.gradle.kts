plugins {
    module
}

dependencies {
    compileOnly(projects.detektApi)
    compileOnly(projects.detektMetrics)
    testImplementation(projects.detektMetrics)
    testImplementation(projects.detektTest)
}
