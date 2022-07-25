plugins {
    id("module")
    alias(libs.plugins.binaryCompatibilityValidator)
}

dependencies {
    api(libs.kotlin.compilerEmbeddable)

    testImplementation(libs.assertj)
    testImplementation(projects.detektTest)
}

tasks.apiDump {
    notCompatibleWithConfigurationCache("https://github.com/Kotlin/binary-compatibility-validator/issues/95")
}

apiValidation {
    ignoredPackages.add("io.github.detekt.psi.internal")
}
