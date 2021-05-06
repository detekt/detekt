plugins {
    module
}

dependencies {
    api(projects.detektApi)
    testImplementation(projects.detektTestUtils)
    testImplementation(testFixtures(projects.detektApi))
}
