plugins {
    module
}

dependencies {
    compileOnly(project(":detekt-api"))
    compileOnly(project(":detekt-metrics"))
    implementation(libs.kotlinx.html) {
        exclude(group = "org.jetbrains.kotlin")
    }
    testImplementation(project(":detekt-metrics"))
    testImplementation(project(":detekt-test-utils"))
    testImplementation(testFixtures(project(":detekt-api")))
}
