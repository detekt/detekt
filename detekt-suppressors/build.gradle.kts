plugins {
    id("module")
}

dependencies {
    api(projects.detektApi)
    api(projects.detektTooling)
    implementation(libs.kotlin.compiler)
    implementation(projects.detektRuleHelpers)

    testImplementation(projects.detektTest)
    testImplementation(projects.detektTestJunit)
    testImplementation(projects.detektTestUtils)
    testImplementation(libs.assertj.core)
}
