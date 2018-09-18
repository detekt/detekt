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
internal class DetektTaskGroovyDslTest : Spek({

	describe("The Detekt Gradle plugin used in a build.gradle file") {
		lateinit var rootDir: File
		beforeEachTest {
			rootDir = createTempDir(prefix = "applyPlugin")
		}
		it("can be applied without any configuration using its task name") {

			val detektConfig = ""

			writeFiles(rootDir, detektConfig)

			// Using a custom "project-cache-dir" to avoid a Gradle error on Windows
			val result = GradleRunner.create()
					.withProjectDir(rootDir)
					.withArguments("--project-cache-dir", createTempDir(prefix = "cache").absolutePath, "detekt", "--stacktrace", "--info")
					.withPluginClasspath()
					.build()

			assertThat(result.output).contains("number of classes: 1")
			assertThat(result.output).contains("Ruleset: comments")
			assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

			assertThat(File(rootDir, "custom/build")).doesNotExist()
			assertThat(File(rootDir, "build/reports/detekt/detekt.xml")).exists()
			assertThat(File(rootDir, "build/reports/detekt/detekt.html")).exists()
		}
		it("can be applied with a version only") {

			val detektConfig = """
				|detekt {
				|	toolVersion = "$VERSION_UNDER_TEST"
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
		it("can change specific report destination") {

			val detektConfig = """
				|detekt {
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
		it("can change general reportsDir") {

			val detektConfig = """
				|detekt {
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
		it("can change reportsDir but overwrite single report destination") {

			val detektConfig = """
				|detekt {
				|	reportsDir = file('build/detekt-reports')
				|	reports {
				|		xml.destination = file('build/xml-reports/custom-detekt.xml')
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

			assertThat(File(rootDir, "build/xml-reports/custom-detekt.xml")).exists()
			assertThat(File(rootDir, "build/detekt-reports/detekt.html")).exists()
		}
		it("can disable a single report type") {
			val detektConfig = """
				|detekt {
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

			assertThat(File(rootDir, "build/reports/detekt/detekt.xml")).exists()
			assertThat(File(rootDir, "build/reports/detekt/detekt.html")).doesNotExist()
		}
		it("can disable a single report type with nested closure") {
			val detektConfig = """
				|detekt {
				|	reports {
				|		xml.enabled = true
				|		html {
				|			enabled = false
				|		}
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

			assertThat(File(rootDir, "build/reports/detekt/detekt.xml")).exists()
			assertThat(File(rootDir, "build/reports/detekt/detekt.html")).doesNotExist()
		}
		it("can configure the input directory") {
			val customSourceLocation = "gensrc/kotlin"
			val detektConfig = """
					|detekt {
					|	input = files(
					|		"$customSourceLocation",
					|		"some/location/thatdoesnotexist"
					|	)
					|}
				"""

			writeFiles(rootDir, detektConfig, customSourceLocation)
			writeConfig(rootDir)

			// Using a custom "project-cache-dir" to avoid a Gradle error on Windows
			val result = GradleRunner.create()
					.withProjectDir(rootDir)
					.withArguments("--project-cache-dir", createTempDir(prefix = "cache").absolutePath, "check", "--stacktrace", "--info")
					.withPluginClasspath()
					.build()

			assertThat(result.output).contains("number of classes: 1")
			assertThat(result.output).contains("--input ${File(rootDir, customSourceLocation).canonicalPath}")
			assertThat(result.task(":check")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
		}
		it("can configure a new custom detekt task") {

			val detektConfig = """
					|task detektFailFast(type: io.gitlab.arturbosch.detekt.Detekt) {
					|	description = "Runs a failfast detekt build."
					|
					|	input = files("src/main/java")
					|	config = files("$rootDir/config.yml")
					|	debug = true
					|	reports {
					|		xml {
					|			destination = file("build/reports/failfast.xml")
					|		}
					|		html.destination = file("build/reports/failfast.html")
					|	}
					|}
				"""

			writeFiles(rootDir, detektConfig)
			writeConfig(rootDir, failFast = true)

			// Using a custom "project-cache-dir" to avoid a Gradle error on Windows
			val result = GradleRunner.create()
					.withProjectDir(rootDir)
					.withArguments("--project-cache-dir", createTempDir(prefix = "cache").absolutePath, "detektFailFast", "--stacktrace", "--info")
					.withPluginClasspath()
					.build()

			assertThat(result.output).contains("number of classes: 1")
			assertThat(result.output).contains("Ruleset: comments")
			assertThat(result.task(":detektFailFast")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

			assertThat(File(rootDir, "build/reports/failfast.xml")).exists()
			assertThat(File(rootDir, "build/reports/failfast.html")).exists()
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
	|	mavenLocal()
	|	jcenter()
	|}
	|
	|$detektConfiguration
	""".trimMargin()

// settings.gradle
private const val settingsFileContent = """include ":custom""""

// src/main/kotlin/MyClass.kt
private val ktFileContent = """
	|internal class MyClass
	|
	""".trimMargin()

private fun writeFiles(root: File, detektConfiguration: String, srcDir: String = "src/main/java") {
	File(root, "build.gradle").writeText(buildFileContent(detektConfiguration))
	File(root, "settings.gradle").writeText(settingsFileContent)
	File(root, srcDir).mkdirs()
	File(root, "$srcDir/MyClass.kt").writeText(ktFileContent)
}

private fun writeConfig(root: File, failFast: Boolean = false) {
	File(root, "config.yml").writeText("""
		|autoCorrect: true
		|failFast: $failFast
		""".trimMargin())
}
