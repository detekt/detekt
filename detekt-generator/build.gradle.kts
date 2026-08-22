@file:Suppress("StringLiteralDuplication")

import java.io.ByteArrayOutputStream

plugins {
    id("com.gradleup.shadow") version "9.6.1"
    id("module")
    id("application")
}

application {
    mainClass = "dev.detekt.generator.Main"
}

val detektCli = configurations.dependencyScope("detektCli")
val detektCliClasspath = configurations.resolvable("detektCliClasspath") { extendsFrom(detektCli) }
val generatedDocumentation = configurations.dependencyScope("generatedDocumentation")
val generatedDocumentationFiles = configurations.resolvable("generatedDocumentationFiles") {
    extendsFrom(generatedDocumentation)

    attributes {
        attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named("generated-documentation"))
    }
}

dependencies {
    implementation(libs.kotlin.compiler)
    implementation(projects.detektApi)
    implementation(projects.detektKotlinAnalysisApi)
    implementation(projects.detektKotlinAnalysisApiStandalone)
    detektCli(projects.detektCli)
    implementation(projects.detektUtils)
    implementation(libs.jcommander)
    generatedDocumentation(projects.detektRulesComments)
    generatedDocumentation(projects.detektRulesComplexity)
    generatedDocumentation(projects.detektRulesCoroutines)
    generatedDocumentation(projects.detektRulesEmptyBlocks)
    generatedDocumentation(projects.detektRulesKtlintWrapper)
    generatedDocumentation(projects.detektRulesExceptions)
    generatedDocumentation(projects.detektRulesLibraries)
    generatedDocumentation(projects.detektRulesNaming)
    generatedDocumentation(projects.detektRulesPerformance)
    generatedDocumentation(projects.detektRulesPotentialBugs)
    generatedDocumentation(projects.detektRulesRuleauthors)
    generatedDocumentation(projects.detektRulesStyle)

    testImplementation(projects.detektTestUtils)
    testImplementation(libs.assertj.core)
    testCompileOnly(libs.jetbrains.annotations)
}

val generateCliOptions = tasks.register<JavaExec>("generateCliOptions") {
    classpath = files(detektCliClasspath)
    mainClass = "dev.detekt.cli.Main"
    args = listOf("--help")

    val cliOptionsOutput = isolated.rootProject.projectDirectory.file("website/docs/gettingstarted/_cli-options.mdx")
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

val copyDocumentation = tasks.register<Copy>("copyDocumentation") {
    from(generatedDocumentationFiles)
    into("$rootDir/website/docs/rules")
}
