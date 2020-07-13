dependencies {
    compileOnly(project(":detekt-api"))
    compileOnly(project(":detekt-metrics"))
    testImplementation(project(":detekt-metrics"))
    testImplementation(project(":detekt-test"))
}
