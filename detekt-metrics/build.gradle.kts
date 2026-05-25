plugins {
    id("module")
}

dependencies {
    api(projects.detektApi)
    api(libs.kotlin.compiler)
    testImplementation(projects.detektTestUtils)
    testImplementation(libs.assertj.core)
}
