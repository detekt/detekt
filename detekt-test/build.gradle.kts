plugins {
    module
}

dependencies {
    api(projects.detektApi)
    api(projects.detektTestUtils)
    compileOnly(libs.assertj)
    implementation(projects.detektCore)
    implementation(projects.detektParser)
}
