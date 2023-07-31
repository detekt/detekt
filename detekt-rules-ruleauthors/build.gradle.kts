plugins {
    id("module")
}

dependencies {
    compileOnly(kotlin("stdlib-jdk8"))
    compileOnly(projects.detektApi)
    testImplementation(projects.detektTest)
    testImplementation(libs.assertj)
}

consumeGeneratedConfig(
    fromProject = projects.detektGenerator,
    fromConfiguration = "generatedRuleauthorsConfig",
    forTask = "processResources"
)

kotlin {
    compilerOptions.freeCompilerArgs.add("-Xcontext-receivers")
}
