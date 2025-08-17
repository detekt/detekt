plugins {
    id("module")
}

dependencies {
    compileOnly(libs.kotlin.stdlib)
    compileOnly(projects.detektApi)
    compileOnly(projects.detektPsiUtils)

    testImplementation(libs.kotlin.compiler)
    testImplementation(projects.detektApi)
    testImplementation(projects.detektTest)
    testImplementation(libs.assertj.core)
}

consumeGeneratedConfig(
    fromProject = projects.detektGenerator,
    fromConfiguration = "generatedRuleauthorsConfig",
    forTask = tasks.processResources
)
