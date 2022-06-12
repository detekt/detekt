plugins {
    id("module")
    alias(libs.plugins.binaryCompatibilityValidator)
}

dependencies {
    api(libs.kotlin.compilerEmbeddable)

    testImplementation(libs.assertj)
    testImplementation(projects.detektTest)
}

apiValidation {
    ignoredPackages.add("io.github.detekt.psi.internal")
}
