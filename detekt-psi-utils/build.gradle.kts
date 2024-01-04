plugins {
    id("module")
    id("public-api")
}

dependencies {
    api(libs.kotlin.compilerEmbeddable)

    testImplementation(libs.assertj)
    testImplementation(libs.bundles.mocking)
    testImplementation(projects.detektTest)
}

detekt {
    config.from("config/detekt.yml")
}

apiValidation {
    ignoredPackages.add("io.github.detekt.psi.internal")
}
