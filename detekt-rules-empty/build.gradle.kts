plugins {
    id("module")
}

dependencies {
    compileOnly(projects.detektApi)
    testImplementation(projects.detektTest)
    testImplementation(libs.bundles.testImplementation)
    testRuntimeOnly(libs.spek.runner)
}
