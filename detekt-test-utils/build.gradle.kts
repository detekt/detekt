plugins {
    module
}

dependencies {
    api(libs.kotlin.stdlibJdk8)
    compileOnly(libs.spek.dsl)
    implementation(projects.detektParser)
    implementation(projects.detektPsiUtils)
    implementation(libs.kotlin.scriptRuntime)
    implementation(libs.kotlin.scriptUtil)
    implementation(libs.kotlin.scriptingCompilerEmbeddable)
}
