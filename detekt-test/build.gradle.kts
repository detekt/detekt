plugins {
    id("module")
    id("public-api")
}

dependencies {
    api(projects.detektApi)
    api(libs.kotlin.compiler)
    implementation(projects.detektUtils)
    implementation(libs.kotlin.reflect)
    implementation(projects.detektCore)

    implementation(projects.detektKotlinAnalysisApi)
    implementation(projects.detektKotlinAnalysisApiStandalone)
    implementation(projects.detektParser)
    implementation(libs.kotlinx.coroutinesCore)
    implementation(libs.kotlinx.coroutinesTest)

    testImplementation(libs.assertj.core)
}

apiValidation {
    ignoredPackages.add("dev.detekt.test.internal")
}
