tasks.build { finalizedBy(":detekt-generator:generateDocumentation") }

dependencies {
    implementation(project(":detekt-api"))
    testImplementation(project(":detekt-test"))
    testImplementation(kotlin("reflect"))
}
