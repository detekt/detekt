plugins {
    module
}

dependencies {
    compileOnly(project(":detekt-api"))
    compileOnly(project(":detekt-tooling"))
    implementation("io.github.detekt.sarif4k:sarif4k")
    testImplementation(project(":detekt-tooling"))
    testImplementation(project(":detekt-test-utils"))
    testImplementation(testFixtures(project(":detekt-api")))
}
