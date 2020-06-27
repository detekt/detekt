dependencies {
    api(project(":detekt-api"))
    api(project(":detekt-metrics"))
    api(project(":detekt-parser"))
    api(project(":detekt-psi-utils"))
    api(project(":detekt-tooling"))

    implementation(project(":detekt-report-html"))
    implementation(project(":detekt-report-txt"))
    implementation(project(":detekt-report-xml"))

    testImplementation(project(":detekt-rules"))
    testImplementation(project(":detekt-test"))
}
