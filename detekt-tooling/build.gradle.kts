plugins {
    id("module")
    id("public-api")
}

dependencies {
    api(projects.detektApi)
    testImplementation(projects.detektTestUtils)
    testImplementation(libs.assertj.core)
}

apiValidation {
    ignoredPackages.add("dev.detekt.tooling.internal")
}
