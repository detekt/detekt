plugins {
    id("module")
}

dependencies {
    implementation(projects.detektApi)
    testImplementation(projects.detektTest)
    testImplementation(libs.bundles.testImplementation)
    testRuntimeOnly(libs.spek.runner)
}
