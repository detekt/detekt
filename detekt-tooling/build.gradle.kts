plugins {
    id("module")
    alias(libs.plugins.binaryCompatibilityValidator)
}

dependencies {
    api(projects.detektApi)
    testImplementation(projects.detektTestUtils)
    testImplementation(libs.bundles.testImplementation)
    testRuntimeOnly(libs.bundles.testRuntime)
}

apiValidation {
    ignoredPackages.add("io.github.detekt.tooling.internal")
}
