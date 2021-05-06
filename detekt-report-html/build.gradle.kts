plugins {
    module
}

dependencies {
    compileOnly(projects.detektApi)
    compileOnly(projects.detektMetrics)
    implementation(libs.kotlinx.html) {
        exclude(group = "org.jetbrains.kotlin")
    }
    testImplementation(projects.detektMetrics)
    testImplementation(projects.detektTestUtils)
    testImplementation(testFixtures(projects.detektApi))
}
