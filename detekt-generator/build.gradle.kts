plugins {
    alias(libs.plugins.shadow)
    id("module")
}

dependencies {
    implementation(projects.detektParser)
    implementation(projects.detektApi)
    implementation(projects.detektRulesEmpty)
    implementation(projects.detektFormatting)
    implementation(projects.detektCli)
    implementation(projects.detektUtils)
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
val formattingConfigFile = "${rootProject.rootDir}/detekt-formatting/src/main/resources/config/config.yml"
val librariesConfigFile = "${rootProject.rootDir}/detekt-rules-libraries/src/main/resources/config/config.yml"
val ruleauthorsConfigFile = "${rootProject.rootDir}/detekt-rules-ruleauthors/src/main/resources/config/config.yml"

val ruleModules = rootProject.subprojects
    .filter { "rules" in it.name }
    .map { it.name }
    .filterNot { it == "detekt-rules" }
    .map { "${rootProject.rootDir}/$it/src/main/kotlin" }

val generateDocumentation by tasks.registering(JavaExec::class) {
    dependsOn(
        tasks.shadowJar,
        ":detekt-api:dokkaHtml",
        ":detekt-rules-libraries:sourcesJar",
        ":detekt-rules-ruleauthors:sourcesJar",
    )
    description = "Generates detekt documentation and the default config.yml based on Rule KDoc"
    group = "documentation"

    inputs.files(
        ruleModules.map { fileTree(it) },
        fileTree("${rootProject.rootDir}/detekt-rules-libraries/src/main/kotlin"),
        fileTree("${rootProject.rootDir}/detekt-rules-ruleauthors/src/main/kotlin"),
        fileTree("${rootProject.rootDir}/detekt-formatting/src/main/kotlin"),
        file("${rootProject.rootDir}/detekt-generator/build/libs/detekt-generator-${Versions.DETEKT}-all.jar"),
    )

    outputs.files(
        fileTree(documentationDir),
        file(defaultConfigFile),
        file(formattingConfigFile),
        file(librariesConfigFile),
        file(ruleauthorsConfigFile),
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
        ruleModules
            .plus("${rootProject.rootDir}/detekt-rules-libraries/src/main/kotlin")
            .plus("${rootProject.rootDir}/detekt-rules-ruleauthors/src/main/kotlin")
            .plus("${rootProject.rootDir}/detekt-formatting/src/main/kotlin")
            .joinToString(","),
        "--documentation",
        documentationDir,
        "--config",
        configDir,
        "--cli-options",
        cliOptionsFile,
    )
}

val generateDocumentationConfigurationLibraries: Configuration by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false
}

val generateDocumentationConfigurationRules: Configuration by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false
}

artifacts {
    add(generateDocumentationConfigurationLibraries.name, file(librariesConfigFile)) {
        builtBy(generateDocumentation)
    }
    add(generateDocumentationConfigurationRules.name, file(ruleauthorsConfigFile)) {
        builtBy(generateDocumentation)
    }
}

val verifyGeneratorOutput by tasks.registering(Exec::class) {
    dependsOn(generateDocumentation)
    description = "Verifies that generated config files are up-to-date"
    commandLine = listOf(
        "git",
        "diff",
        "--quiet",
        defaultConfigFile,
        formattingConfigFile,
        librariesConfigFile,
        ruleauthorsConfigFile,
        deprecationFile,
    )
    isIgnoreExitValue = true

    doLast {
        if (executionResult.get().exitValue == 1) {
            throw GradleException(
                "At least one generated configuration file is not up-to-date. " +
                    "You can execute the generateDocumentation Gradle task " +
                    "to update generated files then commit the changes."
            )
        }
    }
}
