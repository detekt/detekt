plugins {
    module
    alias(libs.plugins.binaryCompatibilityValidator)
}

dependencies {
    implementation(libs.kotlin.compilerEmbeddable)
}
