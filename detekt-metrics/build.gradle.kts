dependencies {
    api(project(":detekt-api"))
    testImplementation(project(":detekt-test-utils"))
    testImplementation(testFixtures(project(":detekt-api")))
}
