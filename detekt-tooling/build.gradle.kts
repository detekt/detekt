plugins {
    id("module")
}

dependencies {
    api(projects.detektApi)
    testImplementation(projects.detektTestUtils)
}
