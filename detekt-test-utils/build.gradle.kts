plugins {
    id("module")
    alias(libs.plugins.binaryCompatibilityValidator)
}

dependencies {
    api(libs.kotlin.stdlibJdk8)
    api(libs.junit.api)
    implementation(projects.detektParser)
    implementation(libs.kotlin.scriptUtil)

    testImplementation(libs.assertj)
    runtimeOnly(libs.kotlin.scriptingCompilerEmbeddable)
}
