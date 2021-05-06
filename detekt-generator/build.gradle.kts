import java.io.ByteArrayOutputStream

plugins {
    module
}

dependencies {
    implementation(projects.detektParser)
    implementation(projects.detektApi)
    implementation(projects.detektRulesEmpty)
    implementation(projects.detektFormatting)
    implementation(projects.detektCli)
    implementation(libs.jcommander)

    testImplementation(projects.detektTestUtils)
}

val documentationDir = "${rootProject.rootDir}/docs/pages/documentation"
val configDir = "${rootProject.rootDir}/detekt-core/src/main/resources"
val cliOptionsFile = "${rootProject.rootDir}/docs/pages/gettingstarted/cli-options.md"
val defaultConfigFile = "$configDir/default-detekt-config.yml"

val ruleModules = rootProject.subprojects
    .filter { "rules" in it.name }
    .map { it.name }
    .filterNot { it == "detekt-rules" }
    .map { "${rootProject.rootDir}/$it/src/main/kotlin" }

val generateDocumentation by tasks.registering(JavaExec::class) {
    dependsOn(tasks.assemble, ":detekt-api:dokkaJekyll")
    description = "Generates detekt documentation and the default config.yml based on Rule KDoc"
    group = "documentation"

    inputs.files(
        ruleModules.map { fileTree(it) },
        fileTree("${rootProject.rootDir}/detekt-formatting/src/main/kotlin"),
        file("${rootProject.rootDir}/detekt-generator/build/libs/detekt-generator-${Versions.DETEKT}-all.jar")
    )

    outputs.files(
        fileTree(documentationDir),
        file(defaultConfigFile),
        file(cliOptionsFile)
    )

    classpath(
        configurations.runtimeClasspath.get(),
        configurations.compileClasspath.get(),
        sourceSets.main.get().output
    )
    main = "io.gitlab.arturbosch.detekt.generator.Main"
    args = listOf(
        "--input",
        ruleModules.joinToString(",") + "," + "${rootProject.rootDir}/detekt-formatting/src/main/kotlin",
        "--documentation",
        documentationDir,
        "--config",
        configDir,
        "--cli-options",
        cliOptionsFile
    )
}

val verifyGeneratorOutput by tasks.registering(Exec::class) {
    dependsOn(generateDocumentation)
    description = "Verifies that the default-detekt-config.yml is up-to-date"
    val configDiff = ByteArrayOutputStream()

    commandLine = listOf("git", "diff", defaultConfigFile)
    standardOutput = configDiff

    if (configDiff.toString().isNotEmpty()) {
        throw GradleException(
            "The default-detekt-config.yml is not up-to-date. " +
                "You can execute the generateDocumentation Gradle task to update it and commit the changed files."
        )
    }
}
