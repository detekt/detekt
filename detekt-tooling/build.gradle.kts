plugins {
    id("module")
    alias(libs.plugins.binaryCompatibilityValidator)
}

dependencies {
    api(projects.detektApi)
    testImplementation(projects.detektTest)
    testImplementation(projects.detektTestUtils)
    testImplementation(libs.assertj)
}

apiValidation {
    ignoredPackages.add("io.github.detekt.tooling.internal")
}
