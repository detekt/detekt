plugins {
    id("module")
    alias(libs.plugins.binaryCompatibilityValidator)
}

dependencies {
    api(projects.detektApi)
    testImplementation(projects.detektTest)
    testImplementation(projects.detektTestUtils)
    testImplementation(libs.assertj)
}

tasks.apiDump {
    notCompatibleWithConfigurationCache("https://github.com/Kotlin/binary-compatibility-validator/issues/95")
}

apiValidation {
    ignoredPackages.add("io.github.detekt.tooling.internal")
}
