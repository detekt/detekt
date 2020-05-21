dependencies {
    api(project(":detekt-api"))
    api(project(":detekt-metrics"))
    api(project(":detekt-parser"))
    api(project(":detekt-psi-utils"))

    testImplementation(project(":detekt-rules"))
    testImplementation(project(":detekt-test"))
}
