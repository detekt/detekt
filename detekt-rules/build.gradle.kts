tasks.build { finalizedBy(":detekt-generator:generateDocumentation") }

dependencies {
    implementation(project(":detekt-api"))

    testImplementation(project(":detekt-test"))

    // For @javax.inject.Inject usage in LongParameterListSpec.kt
    testImplementation("javax.inject:javax.inject:1")
}
