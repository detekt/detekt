plugins {
    id("module")
    alias(libs.plugins.binaryCompatibilityValidator)
}

dependencies {
    api(projects.detektApi)
    testImplementation(projects.detektTest)
    testImplementation(projects.detektTestUtils)
    testImplementation(libs.bundles.testImplementation)
    testRuntimeOnly(libs.spek.runner)
}

apiValidation {
    ignoredPackages.add("io.github.detekt.tooling.internal")
}
