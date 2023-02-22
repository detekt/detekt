plugins {
    id("module")
}

dependencies {
    compileOnly(projects.detektApi)
    testImplementation(projects.detektTest)
    testImplementation(libs.assertj)
}

consumeGeneratedConfig(
    fromProject = projects.detektGenerator,
    fromConfiguration = "generatedRuleauthorsConfig",
    forTask = "processResources"
)

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions.freeCompilerArgs.add("-Xcontext-receivers")
}
