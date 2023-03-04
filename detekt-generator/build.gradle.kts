plugins {
    alias(libs.plugins.shadow)
    id("module")
}

dependencies {
    implementation(projects.detektParser)
    implementation(projects.detektApi)
    implementation(projects.detektCli)
    implementation(projects.detektUtils)
    implementation(libs.jcommander)

    testImplementation(projects.detektCore)
    testImplementation(projects.detektTestUtils)
    testImplementation(libs.assertj)
    testImplementation(libs.reflections)
}

val documentationDir = "$rootDir/website/docs/rules"
val configDir = "$rootDir/detekt-core/src/main/resources"
val cliOptionsFile = "$rootDir/website/docs/gettingstarted/_cli-options.md"
val defaultConfigFile = "$configDir/default-detekt-config.yml"
val deprecationFile = "$configDir/deprecation.properties"
val formattingConfigFile = "$rootDir/detekt-formatting/src/main/resources/config/config.yml"
val librariesConfigFile = "$rootDir/detekt-rules-libraries/src/main/resources/config/config.yml"
val ruleauthorsConfigFile = "$rootDir/detekt-rules-ruleauthors/src/main/resources/config/config.yml"

val generateDocumentation by tasks.registering(JavaExec::class) {
    dependsOn(
        ":detekt-api:dokkaHtml",
        ":detekt-rules-libraries:sourcesJar",
        ":detekt-rules-ruleauthors:sourcesJar",
    )
    description = "Generates detekt documentation and the default config.yml based on Rule KDoc"
    group = "documentation"

    val ruleModules = rootProject.subprojects.asSequence()
        .filter { "rules" in it.name || it.name == "detekt-formatting" }
        .filterNot { it.name == "detekt-rules" }
        .flatMap { it.sourceSets.main.get().kotlin.srcDirs }
        .filter { it.exists() }
        .toList()

    inputs.files(ruleModules)

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
        ruleModules.joinToString(","),
        "--documentation",
        documentationDir,
        "--config",
        configDir,
        "--cli-options",
        cliOptionsFile,
    )
}

val generatedFormattingConfig: Configuration by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false
}

val generatedLibrariesConfig: Configuration by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false
}

val generatedRuleauthorsConfig: Configuration by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false
}

val generatedCoreConfig: Configuration by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false
}

artifacts {
    add(generatedFormattingConfig.name, file(formattingConfigFile)) {
        builtBy(generateDocumentation)
    }
    add(generatedLibrariesConfig.name, file(librariesConfigFile)) {
        builtBy(generateDocumentation)
    }
    add(generatedRuleauthorsConfig.name, file(ruleauthorsConfigFile)) {
        builtBy(generateDocumentation)
    }
    add(generatedCoreConfig.name, file(defaultConfigFile)) {
        builtBy(generateDocumentation)
    }
    add(generatedCoreConfig.name, file(deprecationFile)) {
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
