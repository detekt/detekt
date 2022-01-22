plugins {
    id("module")
}

dependencies {
    implementation(projects.detektApi)
    testImplementation(projects.detektTest)
    testImplementation(libs.assertj)
    testRuntimeOnly(libs.spek.dsl)
}
