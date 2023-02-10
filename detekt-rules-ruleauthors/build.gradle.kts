plugins {
    id("module")
}

val generatedRuleauthorsConfig: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    compileOnly(projects.detektApi)
    testImplementation(projects.detektTest)
    testImplementation(libs.assertj)

    generatedRuleauthorsConfig(
        project(projects.detektGenerator.path, "generatedRuleauthorsConfig")
    )
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions.freeCompilerArgs.add("-Xcontext-receivers")
}

tasks.withType<ProcessResources>().configureEach {
    inputs.files(generatedRuleauthorsConfig)
        .withPropertyName(generatedRuleauthorsConfig.name)
        .withPathSensitivity(PathSensitivity.RELATIVE)
}
