plugins {
    module
}

dependencies {
    // When creating a sample extension, change this dependency to the detekt-api version you build against
    // e.g. io.gitlab.arturbosch.detekt:detekt-api:1.x.x
    implementation(projects.detektApi)
    // When creating a sample extension, change this dependency to the detekt-test version you build against
    // e.g. io.gitlab.arturbosch.detekt:detekt-test:1.x.x
    testImplementation(projects.detektTest)
}
