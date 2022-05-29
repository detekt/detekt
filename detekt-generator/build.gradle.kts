import java.io.ByteArrayOutputStream

plugins {
    id("module")
}

dependencies {
    implementation(projects.detektParser)
    implementation(projects.detektApi)
    implementation(projects.detektRulesEmpty)
    implementation(projects.detektFormatting)
    implementation(projects.detektCli)
    implementation(projects.detektTooling)
    implementation(libs.jcommander)

    testImplementation(projects.detektCore)
    testImplementation(projects.detektTestUtils)
    testImplementation(libs.assertj)
    testImplementation(libs.reflections)
}

val documentationDir = "${rootProject.rootDir}/website/docs/rules"
val configDir = "${rootProject.rootDir}/detekt-core/src/main/resources"
val cliOptionsFile = "${rootProject.rootDir}/website/docs/gettingstarted/_cli-options.md"
val defaultConfigFile = "$configDir/default-detekt-config.yml"
val deprecationFile = "$configDir/deprecation.properties"

val ruleModules = rootProject.subprojects
    .filter { "rules" in it.name }
    .map { it.name }
    .filterNot { it == "detekt-rules" }
    .map { "${rootProject.rootDir}/$it/src/main/kotlin" }

val generateDocumentation by tasks.registering(JavaExec::class) {
    dependsOn(tasks.assemble, ":detekt-api:dokkaHtml")
    description = "Generates detekt documentation and the default config.yml based on Rule KDoc"
    group = "documentation"

    inputs.files(
        ruleModules.map { fileTree(it) },
        fileTree("${rootProject.rootDir}/detekt-formatting/src/main/kotlin"),
        file("${rootProject.rootDir}/detekt-generator/build/libs/detekt-generator-${Versions.DETEKT}-all.jar"),
    )

    outputs.files(
        fileTree(documentationDir),
        file(defaultConfigFile),
        file(deprecationFile),
        file(cliOptionsFile),
    )

    classpath(
        configurations.runtimeClasspath.get(),
        configurations.compileClasspath.get(),
        sourceSets.main.get().output,
    )
    mainClass.set("io.gitlab.arturbosch.detekt.generator.Main")
    args = listOf(
        "--input",
        ruleModules.plus("${rootProject.rootDir}/detekt-formatting/src/main/kotlin").joinToString(","),
        "--documentation",
        documentationDir,
        "--config",
        configDir,
        "--cli-options",
        cliOptionsFile,
    )
}

val verifyGeneratorOutput by tasks.registering(Exec::class) {
    notCompatibleWithConfigurationCache("cannot serialize object of type java.io.ByteArrayOutputStream")
    dependsOn(generateDocumentation)
    description = "Verifies that the default-detekt-config.yml is up-to-date"
    val configDiff = ByteArrayOutputStream()

    commandLine = listOf("git", "diff", defaultConfigFile, deprecationFile)
    standardOutput = configDiff

    doLast {
        if (configDiff.toString().isNotEmpty()) {
            throw GradleException(
                "The default-detekt-config.yml is not up-to-date. " +
                    "You can execute the generateDocumentation Gradle task " +
                    "to update it and commit the changed files."
            )
        }
    }
}
