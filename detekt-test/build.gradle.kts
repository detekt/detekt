plugins {
    id("module")
    alias(libs.plugins.binaryCompatibilityValidator)
}

dependencies {
    api(projects.detektApi)
    api(projects.detektTestUtils)
    implementation(projects.detektUtils)
    compileOnly(libs.assertj)
    implementation(projects.detektCore)

    testImplementation(libs.assertj)
}

tasks.apiDump {
    notCompatibleWithConfigurationCache("https://github.com/Kotlin/binary-compatibility-validator/issues/95")
}
