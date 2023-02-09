plugins {
    id("module")
}

val generatedRulesConfig: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    compileOnly(projects.detektApi)
    testImplementation(projects.detektTest)
    testImplementation(libs.assertj)

    generatedRulesConfig(
        project(":detekt-generator", "generatedRulesConfig")
    )
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions.freeCompilerArgs.add("-Xcontext-receivers")
}

tasks.withType<ProcessResources>().configureEach {
    inputs.files(generatedRulesConfig)
        .withPropertyName(generatedRulesConfig.name)
        .withPathSensitivity(PathSensitivity.RELATIVE)
}
