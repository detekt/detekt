plugins {
    id("module")
    alias(libs.plugins.binaryCompatibilityValidator)
}

dependencies {
    implementation(libs.kotlin.compilerEmbeddable)

    testImplementation(libs.assertj)
    testImplementation(projects.detektTest)
}

apiValidation {
    ignoredPackages.add("io.github.detekt.psi.internal")
}
