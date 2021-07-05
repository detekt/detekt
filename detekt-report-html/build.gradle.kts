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
    testImplementation(libs.mockk)

    constraints {
        testImplementation("net.bytebuddy:byte-buddy:1.11.6") {
            because("version 1.10.14 (pulled in by mockk 1.11.0) is not Java 16 compatible")
        }
    }
}
