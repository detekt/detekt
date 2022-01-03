plugins {
    id("module")
}

dependencies {
    compileOnly(projects.detektApi)
    implementation(projects.detektTooling)
    testImplementation(projects.detektTest)
    testImplementation(libs.bundles.testImplementation)
    testRuntimeOnly(libs.spek.runner)
}
