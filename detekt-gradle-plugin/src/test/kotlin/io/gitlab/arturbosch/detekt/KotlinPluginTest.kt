package io.gitlab.arturbosch.detekt

import org.assertj.core.api.Assertions
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.io.File

/**
 * @author Marvin Ramin
 */
internal class KotlinPluginTest : Spek({

	describe("The Detekt Gradle plugin used in a build.gradle.kts file") {

		it("can be applied without any configuration") {
			val rootDir = createTempDir(prefix = "applyPlugin")

			val detektConfig = ""

			writeFiles(rootDir, detektConfig)

			// Using a custom "project-cache-dir" to avoid a Gradle error on Windows
			val result = GradleRunner.create()
					.withProjectDir(rootDir)
					.withArguments("--project-cache-dir", createTempDir(prefix = "cache").absolutePath, "detektMain", "--stacktrace", "--info")
					.withPluginClasspath()
					.build()

			assertThat(result.output).contains("number of classes: 1")
			assertThat(result.output).contains("Ruleset: comments")
			assertThat(result.task(":detektMain")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

			// Asserts that the "custom" module is not built, and that custom ruleset is not enabled
			assertThat(result.output).doesNotContain("Ruleset: test-custom")
			assertThat(File(rootDir, "custom/build")).doesNotExist()
		}

		it("can be applied with just a basic configuration") {
			val rootDir = createTempDir(prefix = "applyPlugin")

			val detektConfig = """
				"""

			writeFiles(rootDir, detektConfig)

			// Using a custom "project-cache-dir" to avoid a Gradle error on Windows
			val result = GradleRunner.create()
					.withProjectDir(rootDir)
					.withArguments("--project-cache-dir", createTempDir(prefix = "cache").absolutePath, "detektMain", "--stacktrace", "--info")
					.withPluginClasspath()
					.build()

			assertThat(result.output).contains("number of classes: 1")
			assertThat(result.output).contains("Ruleset: comments")
			assertThat(result.task(":detektMain")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

			// Asserts that the "custom" module is not built, and that custom ruleset is not enabled
			assertThat(result.output).doesNotContain("Ruleset: test-custom")
			assertThat(File(rootDir, "custom/build")).doesNotExist()
		}

		it("can be applied with a custom detekt version") {
			val rootDir = createTempDir(prefix = "applyPlugin")

			val detektConfig = """
					|detekt {
					|	toolVersion = "1.0.0.RC6-MARVIN2"
					|}
				"""

			writeFiles(rootDir, detektConfig)
			writeConfig(rootDir)

			// Using a custom "project-cache-dir" to avoid a Gradle error on Windows
			val result = GradleRunner.create()
					.withProjectDir(rootDir)
					.withArguments("--project-cache-dir", createTempDir(prefix = "cache").absolutePath, "detektMain", "--stacktrace", "--info")
					.withPluginClasspath()
					.build()

			assertThat(result.output).contains("number of classes: 1")
			assertThat(result.output).contains("Ruleset: comments")
			assertThat(result.task(":detektMain")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

			// Asserts that the "custom" module is not built, and that custom ruleset is not enabled
			assertThat(result.output).doesNotContain("Ruleset: test-custom")
			assertThat(File(rootDir, "custom/build")).doesNotExist()
		}

		it("can be applied with a custom config file") {
			val rootDir = createTempDir(prefix = "applyPlugin")

			val detektConfig = """
					|detekt {
					|	configFile = file("${rootDir.absolutePath}/config.yml")
					|}
				"""

			writeFiles(rootDir, detektConfig)
			writeConfig(rootDir)

			// Using a custom "project-cache-dir" to avoid a Gradle error on Windows
			val result = GradleRunner.create()
					.withProjectDir(rootDir)
					.withArguments("--project-cache-dir", createTempDir(prefix = "cache").absolutePath, "detektMain", "--stacktrace", "--info")
					.withPluginClasspath()
					.build()

			assertThat(result.output).contains("number of classes: 1")
			assertThat(result.output).contains("Ruleset: comments")
			assertThat(result.task(":detektMain")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

			// Asserts that the "custom" module is not built, and that custom ruleset is not enabled
			assertThat(result.output).doesNotContain("Ruleset: test-custom")
			assertThat(File(rootDir, "custom/build")).doesNotExist()
		}

		it("can be applied with a full config") {
			val rootDir = createTempDir(prefix = "applyPlugin")

			val detektConfig = """
					|detekt {
					|	debug = true
					|	parallel = true
					|	toolVersion = "1.0.0.RC6-MARVIN2"
					|	configFile = file("${rootDir.absolutePath}/config.yml")
					|	baseline = file("${rootDir.absolutePath}/baseline.xml")
					|	filters = ".*/resources/.*, .*/build/.*"
					|}
				"""

			writeFiles(rootDir, detektConfig)
			writeConfig(rootDir)
			writeBaseline(rootDir)

			// Using a custom "project-cache-dir" to avoid a Gradle error on Windows
			val result = GradleRunner.create()
					.withProjectDir(rootDir)
					.withArguments("--project-cache-dir", createTempDir(prefix = "cache").absolutePath, "detektMain", "--stacktrace", "--info")
					.withPluginClasspath()
					.build()

			Assertions.assertThat(result.output).contains("number of classes: 1")
			Assertions.assertThat(result.output).contains("Ruleset: comments")
			Assertions.assertThat(result.task(":detektMain")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

			// Asserts that the "custom" module is not built, and that custom ruleset is not enabled
			Assertions.assertThat(result.output).doesNotContain("Ruleset: test-custom")
			Assertions.assertThat(File(rootDir, "custom/build")).doesNotExist()
		}
	}
})

// build.gradle.kts
private fun getBuildFileContent(detektConfig: String) = """
	|import io.gitlab.arturbosch.detekt.detekt
	|
	|plugins {
	|   `java-library`
	|	id("io.gitlab.arturbosch.detekt")
	|}
	|
	|repositories {
	|	jcenter()
	|	mavenLocal()
	|}
	|
	|$detektConfig
	""".trimMargin()

// src/main/kotlin/MyClass.kt
private val ktFileContent = """
	|class MyClass
	|
	""".trimMargin()

private fun writeFiles(root: File, detektConfig: String) {
	File(root, "build.gradle.kts").writeText(getBuildFileContent(detektConfig))
	File(root, "src/main/java").mkdirs()
	File(root, "src/main/java/MyClass.kt").writeText(ktFileContent)
}

private fun writeConfig(root: File) {
	File(root, "config.yml").writeText("config is here")
}

private fun writeBaseline(root: File) {
	File(root, "baseline.xml").writeText("""
		|<some>
		|	<xml/>
		|</some>
		""")
}
