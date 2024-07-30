plugins {
    id("module")
}

dependencies {
    api(projects.detektApi)
    compileOnly(projects.detektPsiUtils)
    testImplementation(projects.detektTestUtils)
    testImplementation(testFixtures(projects.detektApi))
    testImplementation(libs.assertj.core)
    testRuntimeOnly(projects.detektPsiUtils)
}
