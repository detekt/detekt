plugins {
    id("module")
}

val generatedLibrariesConfig: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    compileOnly(projects.detektApi)
    testImplementation(projects.detektTest)
    testImplementation(libs.assertj)

    generatedLibrariesConfig(
        project(":detekt-generator", "generatedLibrariesConfig")
    )
}

tasks.withType<ProcessResources>().configureEach {
    inputs.files(generatedLibrariesConfig)
        .withPropertyName(generatedLibrariesConfig.name)
        .withPathSensitivity(PathSensitivity.RELATIVE)
}
