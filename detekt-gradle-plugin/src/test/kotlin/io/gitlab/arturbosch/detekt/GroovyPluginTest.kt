package io.gitlab.arturbosch.detekt

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.io.File

/**
 * @author Marvin Ramin
 */
internal class GroovyPluginTest : Spek({

	describe("The Detekt Gradle plugin used in a build.gradle file") {

		// These first two tests will fail until we merge the Gradle rework
		// and release a new version of the CLI artifact
//		it("can be applied without any configuration") {
//			val rootDir = createTempDir(prefix = "applyPlugin")
//
//			val detektConfig = """
//				"""
//
//			writeFiles(rootDir, detektConfig)
//
//			// Using a custom "project-cache-dir" to avoid a Gradle error on Windows
//			val result = GradleRunner.create()
//					.withProjectDir(rootDir)
//					.withArguments("--project-cache-dir", createTempDir(prefix = "cache").absolutePath, "check", "--stacktrace", "--info")
//					.withPluginClasspath()
//					.build()
//
//			assertThat(result.output).contains("number of classes: 1")
//			assertThat(result.output).contains("Ruleset: comments")
//			assertThat(result.task(":check")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
//
//			// Asserts that the "custom" module is not built, and that custom ruleset is not enabled
//			assertThat(result.output).doesNotContain("Ruleset: test-custom")
//			assertThat(File(rootDir, "custom/build")).doesNotExist()
//		}
//
//		it("can be applied with an empty configuration") {
//			val rootDir = createTempDir(prefix = "applyPlugin")
//
//			val detektConfig = """
//				|detekt {
//				|}
//				"""
//
//			writeFiles(rootDir, detektConfig)
//
//			// Using a custom "project-cache-dir" to avoid a Gradle error on Windows
//			val result = GradleRunner.create()
//					.withProjectDir(rootDir)
//					.withArguments("--project-cache-dir", createTempDir(prefix = "cache").absolutePath, "check", "--stacktrace", "--info")
//					.withPluginClasspath()
//					.build()
//
//			assertThat(result.output).contains("number of classes: 1")
//			assertThat(result.output).contains("Ruleset: comments")
//			assertThat(result.task(":check")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
//
//			// Asserts that the "custom" module is not built, and that custom ruleset is not enabled
//			assertThat(result.output).doesNotContain("Ruleset: test-custom")
//			assertThat(File(rootDir, "custom/build")).doesNotExist()
//		}
		it("can be applied with a version only") {
			val rootDir = createTempDir(prefix = "applyPlugin")

			val detektConfig = """
				|detekt {
				|	toolVersion = "1.0.0-GRADLE"
				|}
				"""

			writeFiles(rootDir, detektConfig)

			// Using a custom "project-cache-dir" to avoid a Gradle error on Windows
			val result = GradleRunner.create()
					.withProjectDir(rootDir)
					.withArguments("--project-cache-dir", createTempDir(prefix = "cache").absolutePath, "check", "--stacktrace", "--info")
					.withPluginClasspath()
					.build()

			assertThat(result.output).contains("number of classes: 1")
			assertThat(result.output).contains("Ruleset: comments")
			assertThat(result.task(":check")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

			// Asserts that the "custom" module is not built, and that custom ruleset is not enabled
			assertThat(result.output).doesNotContain("Ruleset: test-custom")
			assertThat(File(rootDir, "custom/build")).doesNotExist()
			assertThat(File(rootDir, "build/reports/detekt/detekt.xml")).exists()
			assertThat(File(rootDir, "build/reports/detekt/detekt.html")).exists()
		}
		it("can disable html report") {
			val rootDir = createTempDir(prefix = "applyPlugin")

			val detektConfig = """
				|detekt {
				|	toolVersion = "1.0.0-GRADLE"
				|}
				|tasks.withType(io.gitlab.arturbosch.detekt.Detekt) {
				|	reports {
				|		html.enabled = false
				|	}
				|}
				"""

			writeFiles(rootDir, detektConfig)

			// Using a custom "project-cache-dir" to avoid a Gradle error on Windows
			val result = GradleRunner.create()
					.withProjectDir(rootDir)
					.withArguments("--project-cache-dir", createTempDir(prefix = "cache").absolutePath, "check", "--stacktrace", "--info")
					.withPluginClasspath()
					.build()

			assertThat(result.output).contains("number of classes: 1")
			assertThat(result.output).contains("Ruleset: comments")
			assertThat(result.task(":check")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

			// Asserts that the "custom" module is not built, and that custom ruleset is not enabled
			assertThat(result.output).doesNotContain("Ruleset: test-custom")
			assertThat(File(rootDir, "custom/build")).doesNotExist()
			assertThat(File(rootDir, "build/reports/detekt/detekt.xml")).exists()
			assertThat(File(rootDir, "build/reports/detekt/detekt.html")).doesNotExist()
		}
		it("can change specific report destination") {
			val rootDir = createTempDir(prefix = "applyPlugin")

			val detektConfig = """
				|detekt {
				|	toolVersion = "1.0.0-GRADLE"
				|}
				|tasks.withType(io.gitlab.arturbosch.detekt.Detekt) {
				|	reports {
				|		html.destination = file('build/somewhere/report.html')
				|	}
				|}
				"""

			writeFiles(rootDir, detektConfig)

			// Using a custom "project-cache-dir" to avoid a Gradle error on Windows
			val result = GradleRunner.create()
					.withProjectDir(rootDir)
					.withArguments("--project-cache-dir", createTempDir(prefix = "cache").absolutePath, "check", "--stacktrace", "--info")
					.withPluginClasspath()
					.build()

			assertThat(result.output).contains("number of classes: 1")
			assertThat(result.output).contains("Ruleset: comments")
			assertThat(result.task(":check")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

			// Asserts that the "custom" module is not built, and that custom ruleset is not enabled
			assertThat(result.output).doesNotContain("Ruleset: test-custom")
			assertThat(File(rootDir, "custom/build")).doesNotExist()
			assertThat(File(rootDir, "build/somewhere/report.html")).exists()
		}
		it("can change reportsDir") {
			val rootDir = createTempDir(prefix = "applyPlugin")

			val detektConfig = """
				|detekt {
				|	toolVersion = "1.0.0-GRADLE"
				|	reportsDir = file('build/detekt-reports')
				|}
				"""

			writeFiles(rootDir, detektConfig)

			// Using a custom "project-cache-dir" to avoid a Gradle error on Windows
			val result = GradleRunner.create()
					.withProjectDir(rootDir)
					.withArguments("--project-cache-dir", createTempDir(prefix = "cache").absolutePath, "check", "--stacktrace", "--info")
					.withPluginClasspath()
					.build()

			assertThat(result.output).contains("number of classes: 1")
			assertThat(result.output).contains("Ruleset: comments")
			assertThat(result.task(":check")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

			assertThat(File(rootDir, "build/detekt-reports/detekt.xml")).exists()
			assertThat(File(rootDir, "build/detekt-reports/detekt.html")).exists()
		}
	}
})

// build.gradle
private fun buildFileContent(detektConfiguration: String) = """
	|import io.gitlab.arturbosch.detekt.DetektPlugin
	|
	|plugins {
	|   id "java-library"
	|   id "io.gitlab.arturbosch.detekt"
	|}
	|
	|repositories {
	|	jcenter()
	|	mavenLocal()
	|}
	|
	|$detektConfiguration
	""".trimMargin()

// settings.gradle
private const val settingsFileContent = """include ":custom""""

// src/main/kotlin/MyClass.kt
private val ktFileContent = """
	|class MyClass
	|
	""".trimMargin()

private fun writeFiles(root: File, detektConfiguration: String) {
	File(root, "build.gradle").writeText(buildFileContent(detektConfiguration))
	File(root, "settings.gradle").writeText(settingsFileContent)
	File(root, "src/main/java").mkdirs()
	File(root, "src/main/java/MyClass.kt").writeText(ktFileContent)
}


private fun writeConfig(root: File) {
	File(root, "config.yml").writeText("""
		|autoCorrect: true
		|failFast: false
		""".trimMargin())
}
