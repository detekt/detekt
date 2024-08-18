plugins {
    id("com.gradleup.shadow") version "8.3.0"
    id("module")
    id("application")
}

application {
    mainClass = "io.gitlab.arturbosch.detekt.generator.Main"
}

val generatedUsage by configurations.dependencyScope("generatedUsage")
val generatedUsageOutput by configurations.resolvable("generatedUsageOutput") {
    extendsFrom(generatedUsage)
}

dependencies {
    implementation(projects.detektParser)
    implementation(projects.detektApi)
    implementation(projects.detektPsiUtils)
    generatedUsage(projects.detektCli) {
        targetConfiguration = "generatedCliUsage"
    }
    implementation(projects.detektUtils)
    implementation(libs.jcommander)

    testImplementation(projects.detektCore)
    testImplementation(projects.detektTestUtils)
    testImplementation(libs.assertj.core)
    testImplementation(libs.classgraph)
    testRuntimeOnly(projects.detektRules)
}

val documentationDir = "$rootDir/website/docs/rules"
val configDir = "$rootDir/detekt-core/src/main/resources"
val defaultConfigFile = "$configDir/default-detekt-config.yml"
val deprecationFile = "$configDir/deprecation.properties"
val formattingConfigFile = "$rootDir/detekt-formatting/src/main/resources/config/config.yml"
val librariesConfigFile = "$rootDir/detekt-rules-libraries/src/main/resources/config/config.yml"
val ruleauthorsConfigFile = "$rootDir/detekt-rules-ruleauthors/src/main/resources/config/config.yml"

val copyDetektCliUsage by tasks.registering(Copy::class) {
    from(generatedUsageOutput) { rename { "_cli-options.md" } }
    destinationDir = rootDir.resolve("website/docs/gettingstarted")
}

tasks.register("generateWebsite") {
    dependsOn(
        copyDetektCliUsage,
        generateDocumentation,
        ":dokkaHtmlMultiModule",
        gradle.includedBuild("detekt-gradle-plugin").task(":dokkaHtml"),
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
    mainClass = "io.gitlab.arturbosch.detekt.generator.Main"
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
