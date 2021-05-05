plugins {
    module
}

dependencies {
    compileOnly(project(":detekt-api"))
    compileOnly(project(":detekt-tooling"))
    implementation(libs.sarif4k)
    testImplementation(project(":detekt-tooling"))
    testImplementation(project(":detekt-test-utils"))
    testImplementation(testFixtures(project(":detekt-api")))
}
