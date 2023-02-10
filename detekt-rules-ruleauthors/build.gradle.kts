plugins {
    id("module")
}

val generatedConfig: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    compileOnly(projects.detektApi)
    testImplementation(projects.detektTest)
    testImplementation(libs.assertj)

    generatedConfig(project(projects.detektGenerator.path, "generatedRuleauthorsConfig"))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions.freeCompilerArgs.add("-Xcontext-receivers")
}

tasks.named("processResources").configure {
    inputs.files(generatedConfig)
        .withPropertyName(generatedConfig.name)
        .withPathSensitivity(PathSensitivity.RELATIVE)
}
