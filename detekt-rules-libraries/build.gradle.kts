plugins {
    id("module")
}

val generateDocumentationConfiguration: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    compileOnly(projects.detektApi)
    testImplementation(projects.detektTest)
    testImplementation(libs.assertj)

    generateDocumentationConfiguration(
        project(
            mapOf(
                "path" to ":detekt-generator",
                "configuration" to "generateDocumentationConfigurationLibraries"
            )
        )
    )
}

tasks.withType<ProcessResources>().configureEach {
    val generateDocumentationFiles: FileCollection = generateDocumentationConfiguration
    inputs.files(generateDocumentationFiles)
        .withPropertyName("generateDocumentationConfiguration")
        .withPathSensitivity(PathSensitivity.RELATIVE)
}
