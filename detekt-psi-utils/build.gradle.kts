plugins {
    id("module")
    alias(libs.plugins.binaryCompatibilityValidator)
}

dependencies {
    api(libs.kotlin.compilerEmbeddable)

    testImplementation(libs.assertj)
    testImplementation(libs.mockk)
    testImplementation(projects.detektTest)
}

apiValidation {
    ignoredPackages.add("io.github.detekt.psi.internal")
}
