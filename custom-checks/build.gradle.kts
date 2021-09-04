plugins {
    id("module")
}

dependencies {
    implementation(projects.detektApi)
    testImplementation(projects.detektTest)
}
