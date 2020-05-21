dependencies {
    api(project(":detekt-api"))
    api(project(":detekt-metrics"))
    api(project(":detekt-parser"))

    testImplementation(project(":detekt-rules"))
    testImplementation(project(":detekt-test"))
}
