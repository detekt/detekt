tasks.build { finalizedBy(":detekt-generator:generateDocumentation") }

dependencies {
    implementation(project(":detekt-api"))
    implementation(project(":detekt-metrics"))

    testImplementation(project(":detekt-test"))
}
