package io.gitlab.arturbosch.detekt

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

private const val SOURCE_DIRECTORY = "src/main/java"

/**
 * @author Markus Schwarz
 */
internal class DetektTaskMultiModuleConsolidationTest : Spek({
	describe("The Detekt Gradle plugin consolidates xml reports in a multi module project") {
		val projectLayout = ProjectLayout(1)
			.withSubmodule("child1", 2)
			.withSubmodule("child2", 4)

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
		it("can be done using the groovy dsl") {

			val mainBuildFileContent: String = """
				|import io.gitlab.arturbosch.detekt.DetektPlugin
				|
				|plugins {
				|   id "java-library"
				|   id "io.gitlab.arturbosch.detekt"
				|}
				|
				|allprojects {
				|	repositories {
				|		mavenLocal()
				|		jcenter()
				|	}
				|}
				|subprojects {
				|	apply plugin: "java-library"
				|	apply plugin: "io.gitlab.arturbosch.detekt"
				|}
				""".trimMargin()

			gradleRunner = DslGradleRunner(projectLayout, "build.gradle", mainBuildFileContent)

		}
		it("can be done using the kotlin dsl") {

			val mainBuildFileContent: String = """
				|import io.gitlab.arturbosch.detekt.detekt
				|
				|plugins {
				|   `java-library`
				|	id("io.gitlab.arturbosch.detekt")
				|}
				|
				|allprojects {
				|	repositories {
				|		mavenLocal()
				|		jcenter()
				|	}
				|}
				|subprojects {
				|	plugins.apply("java-library")
				|	plugins.apply("io.gitlab.arturbosch.detekt")
				|}
				""".trimMargin()

			gradleRunner = DslGradleRunner(projectLayout, "build.gradle.kts", mainBuildFileContent)
		}
	}
})
