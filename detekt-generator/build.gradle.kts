import java.io.ByteArrayOutputStream

plugins {
    id("com.gradleup.shadow") version "9.0.1"
    id("module")
    id("application")
}

application {
    mainClass = "dev.detekt.generator.Main"
}

val detektCli by configurations.dependencyScope("detektCli")
val detektCliClasspath by configurations.resolvable("detektCliClasspath") {
    extendsFrom(detektCli)
}

dependencies {
    implementation(libs.kotlin.compiler)
    implementation(projects.detektParser)
    implementation(projects.detektApi)
    implementation(projects.detektPsiUtils)
    detektCli(projects.detektCli)
    implementation(projects.detektUtils)
    implementation(libs.jcommander)

    testImplementation(projects.detektTestUtils)
    testImplementation(libs.assertj.core)
}

val generateCliOptions by tasks.registering(JavaExec::class) {
    classpath = detektCliClasspath
    mainClass = "dev.detekt.cli.Main"
    args = listOf("--help")

    val cliOptionsOutput = isolated.rootProject.projectDirectory.file("website/docs/gettingstarted/_cli-options.md")
    outputs.file(cliOptionsOutput)
    doFirst {
        standardOutput = ByteArrayOutputStream()
    }
    doLast {
        cliOptionsOutput.asFile.apply {
            writeText("```\n")
            appendBytes((standardOutput as ByteArrayOutputStream).toByteArray())
            appendText("```\n")
        }
    }
}

val documentationDir = "$rootDir/website/docs/rules"
val configDir = "$rootDir/detekt-core/src/main/resources"
val defaultConfigFile = "$configDir/default-detekt-config.yml"
val deprecationFile = "$configDir/deprecation.properties"
val formattingConfigFile = "$rootDir/detekt-formatting/src/main/resources/config/config.yml"
val librariesConfigFile = "$rootDir/detekt-rules-libraries/src/main/resources/config/config.yml"
val ruleauthorsConfigFile = "$rootDir/detekt-rules-ruleauthors/src/main/resources/config/config.yml"

tasks.register("generateWebsite") {
    dependsOn(
        generateCliOptions,
        generateDocumentation,
        ":dokkaGenerate",
        gradle.includedBuild("detekt-gradle-plugin").task(":dokkaGenerate"),
    )
}

val generateDocumentation by tasks.registering(JavaExec::class) {
    dependsOn(
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

    classpath(
        configurations.runtimeClasspath.get(),
        configurations.compileClasspath.get(),
        sourceSets.main.get().output,
    )
    mainClass = "dev.detekt.generator.Main"
    args = listOf(
        "--input",
        ruleModules.joinToString(","),
        "--documentation",
        documentationDir,
        "--config",
        configDir,
        "--replace",
        "<ktlintVersion/>=${libs.versions.ktlint.get()}"
    )
}

val generatedFormattingConfig by configurations.consumable("generatedFormattingConfig")
val generatedLibrariesConfig by configurations.consumable("generatedLibrariesConfig")
val generatedRuleauthorsConfig by configurations.consumable("generatedRuleauthorsConfig")
val generatedCoreConfig by configurations.consumable("generatedCoreConfig")

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
