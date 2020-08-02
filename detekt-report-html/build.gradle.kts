dependencies {
    compileOnly(project(":detekt-api"))
    compileOnly(project(":detekt-metrics"))
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm") {
        exclude(group = "org.jetbrains.kotlin")
    }
    testImplementation(project(":detekt-metrics"))
    testImplementation(project(":detekt-test-utils"))
    testImplementation(testFixtures(project(":detekt-api")))
}
