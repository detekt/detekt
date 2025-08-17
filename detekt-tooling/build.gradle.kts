plugins {
    id("module")
    id("public-api")
}

dependencies {
    api(projects.detektApi)
    api(libs.kotlin.compiler)
    testImplementation(projects.detektTestUtils)
    testImplementation(libs.assertj.core)
}

apiValidation {
    ignoredPackages.add("dev.detekt.tooling.internal")
}
