dependencies {
    implementation("org.yaml:snakeyaml")
    implementation(project(":detekt-api"))
    implementation(project(":detekt-metrics"))
    implementation(project(":detekt-parser"))
    implementation(project(":detekt-psi-utils"))
    implementation(project(":detekt-tooling"))
    implementation(project(":detekt-report-html"))
    implementation(project(":detekt-report-txt"))
    implementation(project(":detekt-report-xml"))
    implementation(project(":detekt-report-sarif"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")

    testImplementation(project(":detekt-rules"))
    testImplementation(project(":detekt-test"))
    testImplementation(testFixtures(project(":detekt-api")))
}
