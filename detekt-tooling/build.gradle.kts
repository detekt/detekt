plugins {
    id("module")
    id("public-api")
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
