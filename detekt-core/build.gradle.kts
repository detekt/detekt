dependencies {
    api(project(":detekt-api"))
    api(project(":detekt-metrics"))

    testImplementation(project(":detekt-rules"))
    testImplementation(project(":detekt-test"))
}
