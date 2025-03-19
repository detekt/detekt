plugins {
    id("module")
    id("public-api")
}

dependencies {
    api(libs.kotlin.compiler)

    testImplementation(libs.assertj.core)
    testImplementation(projects.detektTest)
    testImplementation(projects.detektTestUtils)
}

detekt {
    config.from("config/detekt.yml")
}

apiValidation {
    ignoredPackages.add("io.github.detekt.psi.internal")
}
