plugins {
    id("module")
}

dependencies {
    compileOnly(projects.detektApi)
    implementation(projects.detektTooling)
    testImplementation(projects.detektTest)
    testImplementation(libs.assertj)
}
