@file:Suppress("StringLiteralDuplication")

plugins {
    id("module")
}

val generatedConfig by configurations.dependencyScope("generatedConfig")
val generatedConfigFiles = configurations.resolvable("generatedConfigFiles") { extendsFrom(generatedConfig) }
val generatedDeprecations by configurations.dependencyScope("generatedDeprecations")
val generatedDeprecationsFiles = configurations.resolvable("generatedDeprecationsFiles") {
    extendsFrom(generatedDeprecations)
}

dependencies {
    api(projects.detektApi)
    api(projects.detektTooling)
    api(libs.kotlin.compiler)
    implementation(projects.detektKotlinAnalysisApiStandalone)
    implementation(libs.snakeyaml.engine)
    implementation(libs.kotlin.reflect)
    implementation(projects.detektMetrics)
    implementation(projects.detektParser)
    implementation(projects.detektPsiUtils)
    implementation(projects.detektUtils)
    generatedConfig(projects.detektRulesComments) { targetConfiguration = "generatedConfig" }
    generatedConfig(projects.detektRulesComplexity) { targetConfiguration = "generatedConfig" }
    generatedConfig(projects.detektRulesCoroutines) { targetConfiguration = "generatedConfig" }
    generatedConfig(projects.detektRulesEmptyBlocks) { targetConfiguration = "generatedConfig" }
    generatedConfig(projects.detektRulesExceptions) { targetConfiguration = "generatedConfig" }
    generatedConfig(projects.detektRulesNaming) { targetConfiguration = "generatedConfig" }
    generatedConfig(projects.detektRulesPerformance) { targetConfiguration = "generatedConfig" }
    generatedConfig(projects.detektRulesPotentialBugs) { targetConfiguration = "generatedConfig" }
    generatedConfig(projects.detektRulesStyle) { targetConfiguration = "generatedConfig" }
    generatedDeprecations(projects.detektRulesComments) { targetConfiguration = "generatedDeprecations" }
    generatedDeprecations(projects.detektRulesComplexity) { targetConfiguration = "generatedDeprecations" }
    generatedDeprecations(projects.detektRulesCoroutines) { targetConfiguration = "generatedDeprecations" }
    generatedDeprecations(projects.detektRulesEmptyBlocks) { targetConfiguration = "generatedDeprecations" }
    generatedDeprecations(projects.detektRulesKtlintWrapper) { targetConfiguration = "generatedDeprecations" }
    generatedDeprecations(projects.detektRulesExceptions) { targetConfiguration = "generatedDeprecations" }
    generatedDeprecations(projects.detektRulesLibraries) { targetConfiguration = "generatedDeprecations" }
    generatedDeprecations(projects.detektRulesNaming) { targetConfiguration = "generatedDeprecations" }
    generatedDeprecations(projects.detektRulesPerformance) { targetConfiguration = "generatedDeprecations" }
    generatedDeprecations(projects.detektRulesPotentialBugs) { targetConfiguration = "generatedDeprecations" }
    generatedDeprecations(projects.detektRulesRuleauthors) { targetConfiguration = "generatedDeprecations" }
    generatedDeprecations(projects.detektRulesStyle) { targetConfiguration = "generatedDeprecations" }

    testRuntimeOnly(projects.detektRules)
    testImplementation(projects.detektReportHtml)
    testImplementation(projects.detektReportMarkdown)
    testImplementation(projects.detektReportCheckstyle)
    testImplementation(projects.detektReportSarif)
    testImplementation(projects.detektTest)
    testImplementation(projects.detektTestUtils)
    testImplementation(testFixtures(projects.detektApi))
    testImplementation(libs.classgraph)
    testImplementation(libs.assertj.core)
    testRuntimeOnly(libs.slf4j.simple)
}

val generateDefaultDetektConfig by tasks.registering {
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
                            - 'DetektProgressListener'
                          # - 'KtFileCountProcessor'
                          # - 'PackageCountProcessor'
                          # - 'ClassCountProcessor'
                          # - 'FunctionCountProcessor'
                          # - 'PropertyCountProcessor'
                          # - 'ProjectComplexityProcessor'
                          # - 'ProjectCognitiveComplexityProcessor'
                          # - 'ProjectLLOCProcessor'
                          # - 'ProjectCLOCProcessor'
                          # - 'ProjectLOCProcessor'
                          # - 'ProjectSLOCProcessor'
                          # - 'LicenseHeaderLoaderExtension'
                        
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

val generateDeprecationList by tasks.registering {
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

tasks.processResources.configure {
    inputs.files(generateDefaultDetektConfig)
    inputs.files(generateDeprecationList)
}

tasks.sourcesJar.configure {
    inputs.files(generateDefaultDetektConfig)
    inputs.files(generateDeprecationList)
}

val verifyGeneratorOutput by tasks.registering(Exec::class) {
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
