@file:Suppress("StringLiteralDuplication")

import java.io.ByteArrayOutputStream

plugins {
    id("com.gradleup.shadow") version "9.3.2"
    id("module")
    id("application")
}

application {
    mainClass = "dev.detekt.generator.Main"
}

val detektCli by configurations.dependencyScope("detektCli")
val detektCliClasspath by configurations.resolvable("detektCliClasspath") { extendsFrom(detektCli) }
val generatedDocumentation by configurations.dependencyScope("generatedDocumentation")
val generatedDocumentationFiles = configurations.resolvable("generatedDocumentationFiles") {
    extendsFrom(generatedDocumentation)
}

dependencies {
    implementation(libs.kotlin.compiler)
    implementation(projects.detektApi)
    implementation(projects.detektKotlinAnalysisApi)
    implementation(projects.detektKotlinAnalysisApiStandalone)
    detektCli(projects.detektCli)
    implementation(projects.detektUtils)
    implementation(libs.jcommander)
    generatedDocumentation(projects.detektRulesComments) { targetConfiguration = "generatedDocumentation" }
    generatedDocumentation(projects.detektRulesComplexity) { targetConfiguration = "generatedDocumentation" }
    generatedDocumentation(projects.detektRulesCoroutines) { targetConfiguration = "generatedDocumentation" }
    generatedDocumentation(projects.detektRulesEmptyBlocks) { targetConfiguration = "generatedDocumentation" }
    generatedDocumentation(projects.detektRulesKtlintWrapper) { targetConfiguration = "generatedDocumentation" }
    generatedDocumentation(projects.detektRulesExceptions) { targetConfiguration = "generatedDocumentation" }
    generatedDocumentation(projects.detektRulesLibraries) { targetConfiguration = "generatedDocumentation" }
    generatedDocumentation(projects.detektRulesNaming) { targetConfiguration = "generatedDocumentation" }
    generatedDocumentation(projects.detektRulesPerformance) { targetConfiguration = "generatedDocumentation" }
    generatedDocumentation(projects.detektRulesPotentialBugs) { targetConfiguration = "generatedDocumentation" }
    generatedDocumentation(projects.detektRulesRuleauthors) { targetConfiguration = "generatedDocumentation" }
    generatedDocumentation(projects.detektRulesStyle) { targetConfiguration = "generatedDocumentation" }

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

tasks.register("generateWebsite") {
    description = "Generates detekt website"
    group = "documentation"

    dependsOn(
        generateCliOptions,
        copyDocumentation,
        ":dokkaGenerate",
        gradle.includedBuild("detekt-gradle-plugin").task(":dokkaGenerate"),
    )
}

val copyDocumentation by tasks.registering(Copy::class) {
    from(generatedDocumentationFiles)
    into("$rootDir/website/docs/rules")
}
