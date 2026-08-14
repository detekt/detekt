plugins {
    id("module")
}

dependencies {
    compileOnly(projects.detektApi)

    testImplementation(projects.detektApi)
    testImplementation(projects.detektTest)
    testImplementation(projects.detektTestUtils)
    testImplementation(libs.assertj.core)
    testImplementation(libs.kotlin.compiler)
}
