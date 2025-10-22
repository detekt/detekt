plugins {
    id("module")
}

dependencies {
    compileOnly(projects.detektApi)
    compileOnly(projects.detektMetrics)
    implementation(projects.detektUtils)
    implementation(libs.kotlinx.html) {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
    }

    testImplementation(libs.kotlin.compiler)
    testImplementation(projects.detektMetrics)
    testImplementation(projects.detektTestUtils)
    testImplementation(testFixtures(projects.detektApi))
    testImplementation(libs.assertj.core)
}
