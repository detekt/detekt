dependencies {
    compileOnly(project(":detekt-api"))
    testImplementation(project(":detekt-api"))
    testImplementation(project(":detekt-test-utils"))
}
