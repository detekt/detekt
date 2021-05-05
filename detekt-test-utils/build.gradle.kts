plugins {
    module
}

dependencies {
    api(libs.kotlin.stdlibJdk8)
    compileOnly(libs.spek.dsl)
    implementation(project(":detekt-parser"))
    implementation(project(":detekt-psi-utils"))
    implementation(libs.kotlin.scriptRuntime)
    implementation(libs.kotlin.scriptUtil)
    implementation(libs.kotlin.scriptingCompilerEmbeddable)
}
