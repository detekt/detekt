plugins {
    id("module")
    id("public-api")
}

dependencies {
    api(libs.kotlin.compiler)

    implementation(projects.detektKotlinAnalysisApi)

    testImplementation(libs.assertj.core)
    testImplementation(projects.detektTest)
    testImplementation(projects.detektTestUtils)
}

detekt {
    config.from("config/detekt.yml")
}

apiValidation {
    ignoredPackages.add("dev.detekt.psi.internal")
}
