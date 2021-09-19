plugins {
    id("module")
    alias(libs.plugins.binaryCompatibilityValidator)
}

dependencies {
    implementation(libs.kotlin.compilerEmbeddable)

    testImplementation(libs.bundles.testImplementation)
    testRuntimeOnly(libs.spek.runner)
}
