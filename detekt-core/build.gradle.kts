@file:Suppress("StringLiteralDuplication")

plugins {
    id("module")
}

val generatedConfig = configurations.dependencyScope("generatedConfig")
val generatedConfigFiles = configurations.resolvable("generatedConfigFiles") {
    extendsFrom(generatedConfig)

    attributes {
        attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named("generated-config"))
    }
}
val generatedDeprecations = configurations.dependencyScope("generatedDeprecations")
val generatedDeprecationsFiles = configurations.resolvable("generatedDeprecationsFiles") {
    extendsFrom(generatedDeprecations)

    attributes {
        attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named("generated-deprecations"))
    }
}

dependencies {
    api(projects.detektApi)
    api(projects.detektTooling)
    api(libs.kotlin.compiler)
    implementation(projects.detektKotlinAnalysisApi)
    implementation(projects.detektKotlinAnalysisApiStandalone)
    implementation(libs.snakeyaml.engine)
    implementation(libs.kotlin.reflect)
    implementation(projects.detektParser)
    implementation(projects.detektPsiUtils)
    implementation(projects.detektUtils)
    generatedConfig(projects.detektRulesComments)
    generatedConfig(projects.detektRulesComplexity)
    generatedConfig(projects.detektRulesCoroutines)
    generatedConfig(projects.detektRulesEmptyBlocks)
    generatedConfig(projects.detektRulesExceptions)
    generatedConfig(projects.detektRulesNaming)
    generatedConfig(projects.detektRulesPerformance)
    generatedConfig(projects.detektRulesPotentialBugs)
    generatedConfig(projects.detektRulesStyle)
    generatedDeprecations(projects.detektRulesComments)
    generatedDeprecations(projects.detektRulesComplexity)
    generatedDeprecations(projects.detektRulesCoroutines)
    generatedDeprecations(projects.detektRulesEmptyBlocks)
    generatedDeprecations(projects.detektRulesKtlintWrapper)
    generatedDeprecations(projects.detektRulesExceptions)
    generatedDeprecations(projects.detektRulesLibraries)
    generatedDeprecations(projects.detektRulesNaming)
    generatedDeprecations(projects.detektRulesPerformance)
    generatedDeprecations(projects.detektRulesPotentialBugs)
    generatedDeprecations(projects.detektRulesRuleauthors)
    generatedDeprecations(projects.detektRulesStyle)

    testRuntimeOnly(projects.detektRules)
    runtimeOnly(projects.detektMetrics)
    testImplementation(projects.detektReportHtml)
    testImplementation(projects.detektReportMarkdown)
    testImplementation(projects.detektReportCheckstyle)
    testImplementation(projects.detektReportSarif)
    testImplementation(projects.detektTest)
    testImplementation(projects.detektTestJunit)
    testImplementation(projects.detektTestUtils)
    testImplementation(testFixtures(projects.detektApi))
    testImplementation(libs.classgraph)
    testImplementation(libs.assertj.core)
    testCompileOnly(libs.jetbrains.annotations)
    testRuntimeOnly(libs.slf4j.simple)
}

val generateDefaultDetektConfig = tasks.register("generateDefaultDetektConfig") {
    inputs.files(generatedConfigFiles)
        .withPropertyName(generatedConfig.name)
        .withPathSensitivity(PathSensitivity.RELATIVE)

    val output = sourceSets.main.map { it.resources.srcDirs.single().resolve("default-detekt-config.yml") }
    outputs.file(output)

    doLast {
        output.get().outputStream().use { outputStream ->
            outputStream.writer().use { writer ->
                writer.write(
                    """
                        config:
                          validation: true
                          warningsAsErrors: false
                          checkExhaustiveness: false
                          # when writing own rules with new properties, exclude the property path e.g.: ['my_rule_set', '.*>.*>[my_property]']
                          excludes: []
                        
                        processors:
                          active: true
                          exclude:
                          # - 'KtFileCountProcessor'
                          # - 'PackageCountProcessor'
                          # - 'ClassCountProcessor'
                          # - 'FunctionCountProcessor'
                          # - 'PropertyCountProcessor'
                          # - 'ProjectCyclomaticComplexityProcessor'
                          # - 'ProjectCognitiveComplexityProcessor'
                          # - 'ProjectLLOCProcessor'
                          # - 'ProjectCLOCProcessor'
                          # - 'ProjectLOCProcessor'
                          # - 'ProjectSLOCProcessor'
                        
                        console-reports:
                          active: true
                          exclude:
                             - 'ProjectStatisticsReport'
                             - 'ComplexityReport'
                             - 'NotificationReport'
                             - 'IssuesReport'
                             - 'FileBasedIssuesReport'
                          #  - 'LiteIssuesReport'
                        
                    """.trimIndent()
                )
                writer.flush()
                inputs.files.asFileTree.sorted().forEach {
                    outputStream.write('\n'.code)
                    it.inputStream().use { inputStream -> inputStream.copyTo(outputStream) }
                }
            }
        }
    }
}

val generateDeprecationList = tasks.register("generateDeprecationList") {
    inputs.files(generatedDeprecationsFiles)
        .withPropertyName(generatedDeprecations.name)
        .withPathSensitivity(PathSensitivity.RELATIVE)

    val output = sourceSets.main.map { it.resources.srcDirs.single().resolve("deprecation.properties") }
    outputs.file(output)

    doLast {
        output.get().outputStream().use { outputStream ->
            inputs.files.asFileTree.forEach {
                it.inputStream().use { inputStream -> inputStream.copyTo(outputStream) }
            }
        }
    }
}

tasks.processResources {
    inputs.files(generateDefaultDetektConfig, generateDeprecationList)
}

tasks.sourcesJar {
    inputs.files(generateDefaultDetektConfig, generateDeprecationList)
}

tasks.register<Exec>("verifyGeneratorOutput") {
    dependsOn(generateDefaultDetektConfig, generateDeprecationList)
    description = "Verifies that generated config files are up-to-date"
    commandLine = listOf(
        "git",
        "diff",
        "--quiet",
        sourceSets.main.map { it.resources.srcDirs.single().resolve("default-detekt-config.yml") }.get().toString(),
        sourceSets.main.map { it.resources.srcDirs.single().resolve("deprecation.properties") }.get().toString(),
    )
    isIgnoreExitValue = true

    doLast {
        if (executionResult.get().exitValue == 1) {
            throw GradleException(
                "At least one generated configuration file is not up-to-date. " +
                    "You can execute the Gradle tasks generateDefaultDetektConfig and generateDeprecationList " +
                    "to update the generated files and then commit the changes."
            )
        }
    }
}
