package io.gitlab.arturbosch.detekt

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * @author Markus Schwarz
 */
internal class DetektTaskMultiModuleConsolidationTest : Spek({
    describe("The Detekt Gradle plugin consolidates xml reports in a multi module project") {
        val projectLayout = ProjectLayout(
            numberOfSourceFilesInRootPerSourceDir = 1,
            numberOfCodeSmellsInRootPerSourceDir = 1
        )
            .withSubmodule(name = "child1", numberOfSourceFilesPerSourceDir = 2, numberOfCodeSmells = 1)
            .withSubmodule(name = "child2", numberOfSourceFilesPerSourceDir = 4, numberOfCodeSmells = 1)

        lateinit var gradleRunner: DslGradleRunner

        afterEachTest {
            gradleRunner.setupProject()
            gradleRunner.runDetektTaskAndCheckResult { result ->
                assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                projectLayout.submodules.forEach { submodule ->
                    assertThat(result.task(":${submodule.name}:detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                }

                val consolidatedReport = projectFile("build/reports/detekt/detekt.xml").readLines()
                val codeSmellsPerProject = 1
                val headerAndFooter = 3
                val linesPerSubModule = 3 * codeSmellsPerProject
                val linesInMainProject = 3 * codeSmellsPerProject

                val expectedLinesInConsolidatedReport =
                    headerAndFooter +
                        linesInMainProject +
                        (projectLayout.submodules.size * linesPerSubModule)
                assertThat(consolidatedReport).hasSize(expectedLinesInConsolidatedReport)
            }
        }
        it("using the groovy dsl") {

            val mainBuildFileContent: String = """
                |plugins {
                |   id "io.gitlab.arturbosch.detekt"
                |   id "org.jetbrains.kotlin.jvm"
                |}
                |
                |allprojects {
                |    repositories {
                |        mavenLocal()
                |        jcenter()
                |    }
                |}
                |subprojects {
                |    apply plugin: "io.gitlab.arturbosch.detekt"
                |    apply plugin: "kotlin"
                |}
                """.trimMargin()

            gradleRunner = DslGradleRunner(projectLayout, "build.gradle", mainBuildFileContent)
        }
        it("using the kotlin dsl") {

            val mainBuildFileContent: String = """
                |plugins {
                |    kotlin("jvm")
                |    id("io.gitlab.arturbosch.detekt")
                |}
                |
                |allprojects {
                |    repositories {
                |        mavenLocal()
                |        jcenter()
                |    }
                |}
                |subprojects {
                |    plugins.apply("kotlin")
                |    plugins.apply("io.gitlab.arturbosch.detekt")
                |}
                """.trimMargin()

            gradleRunner = DslGradleRunner(projectLayout, "build.gradle.kts", mainBuildFileContent)
        }
    }
})
