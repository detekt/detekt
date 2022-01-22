plugins {
    id("module")
    alias(libs.plugins.binaryCompatibilityValidator)
}

dependencies {
    api(libs.kotlin.stdlibJdk8)
    compileOnly(libs.spek.dsl)
    implementation(projects.detektParser)
    implementation(libs.kotlin.scriptUtil)
    implementation(libs.kotlin.scriptingCompilerEmbeddable)
    implementation(libs.junit.api)
}
