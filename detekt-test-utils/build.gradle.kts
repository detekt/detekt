plugins {
    id("module")
    alias(libs.plugins.binaryCompatibilityValidator)
}

dependencies {
    api(libs.kotlin.stdlibJdk8)
    api(libs.junit.api)
    compileOnly(libs.spek.dsl)
    implementation(projects.detektParser)
    implementation(libs.kotlin.scriptUtil)

    testImplementation(libs.assertj)
    runtimeOnly(libs.kotlin.scriptingCompilerEmbeddable)
}

tasks.apiDump {
    notCompatibleWithConfigurationCache("https://github.com/Kotlin/binary-compatibility-validator/issues/95")
}
