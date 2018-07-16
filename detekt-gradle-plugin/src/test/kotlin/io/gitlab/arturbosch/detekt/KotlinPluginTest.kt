package io.gitlab.arturbosch.detekt

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.xit
import java.io.File

/**
 * @author Marvin Ramin
 */
internal class KotlinPluginTest : Spek({

	describe("The Detekt Gradle plugin used in a build.gradle.kts file") {

		// This test fails right now because it doesn't specify a toolVersion, which will pick latest CLI
		// That is incompatible until we merge the Gradle Plugin rework
//		it("can be applied without any configuration") {
//			val rootDir = createTempDir(prefix = "applyPlugin")
//
//			val detektConfig = ""
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

		// This test fails right now because it doesn't specify a toolVersion, which will pick latest CLI
		// That is incompatible until we merge the Gradle Plugin rework
//		it("can be applied with an empty configuration") {
//			val rootDir = createTempDir(prefix = "applyPlugin")
//
//			val detektConfig = """
//				|detekt {
//				|
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

		it("can be applied with a custom detekt version") {
			val rootDir = createTempDir(prefix = "applyPlugin")

			val detektConfig = """
					|detekt {
					|	toolVersion = "1.0.0-GRADLE"
					|}
				"""

			writeFiles(rootDir, detektConfig)
			writeConfig(rootDir)

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
		}

		it("can be applied with a custom config file") {
			val rootDir = createTempDir(prefix = "applyPlugin")

			val detektConfig = """
					|detekt {
					|	toolVersion = "1.0.0-GRADLE"
					|	configFile = file("${rootDir.absolutePath}/config.yml")
					|}
				"""

			writeFiles(rootDir, detektConfig)
			writeConfig(rootDir)

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
		}

		it("can be applied with a full config") {
			val rootDir = createTempDir(prefix = "applyPlugin")

			val detektConfig = """
					|detekt {
					|	debug = true
					|	parallel = true
					|	toolVersion = "1.0.0-GRADLE"
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
					.withArguments("--project-cache-dir", createTempDir(prefix = "cache").absolutePath, "check", "--stacktrace", "--info")
					.withPluginClasspath()
					.build()

			assertThat(result.output).contains("number of classes: 1")
			assertThat(result.output).contains("Ruleset: comments")
			assertThat(result.task(":check")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

			// Asserts that the "custom" module is not built, and that custom ruleset is not enabled
			assertThat(result.output).doesNotContain("Ruleset: test-custom")
			assertThat(File(rootDir, "custom/build")).doesNotExist()
		}

		xit("can configure a new custom detekt task", "fails with some internal api error") {
			val rootDir = createTempDir(prefix = "applyPlugin")

			val detektConfig = """
					|detekt {
					|	toolVersion = "1.0.0-GRADLE"
					|}
					|
					|tasks {
					| 	"detektFailFast"(io.gitlab.arturbosch.detekt.Detekt::class) {
					|		description = "Runs a failfast detekt build."
					|
					|		source = java.sourceSets["main"].allSource
					|		configFile = file("$rootDir/config.yml")
					|	}
					|}
				"""

			writeFiles(rootDir, detektConfig)
			writeConfig(rootDir)
			writeBaseline(rootDir)

			// Using a custom "project-cache-dir" to avoid a Gradle error on Windows
			val result = GradleRunner.create()
					.withProjectDir(rootDir)
					.withArguments("--project-cache-dir", createTempDir(prefix = "cache").absolutePath, "detektFailFast", "--stacktrace", "--info")
					.withPluginClasspath()
					.build()

			assertThat(result.output).contains("number of classes: 1")
			assertThat(result.output).contains("Ruleset: comments")
			assertThat(result.task(":check")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

			// Asserts that the "custom" module is not built, and that custom ruleset is not enabled
			assertThat(result.output).doesNotContain("Ruleset: test-custom")
			assertThat(File(rootDir, "custom/build")).doesNotExist()
		}

		it("can configure reports") {
			val rootDir = createTempDir(prefix = "applyPlugin")

			val detektConfig = """
					|detekt {
					|	toolVersion = "1.0.0-GRADLE"
					|}
					|
					|tasks.withType<io.gitlab.arturbosch.detekt.Detekt> {
					|	reports {
					|		xml.isEnabled = true
					|		xml.destination = file("build/reports/detekt.xml")
					|		html.isEnabled = true
					|		html.destination = file("build/reports/detekt.html")
					|	}
					|}
				"""

			writeFiles(rootDir, detektConfig)
			writeConfig(rootDir)
			writeBaseline(rootDir)

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
	File(root, "config.yml").writeText("""
		|autoCorrect: true
		|failFast: false
		""".trimMargin())
}

private fun writeBaseline(root: File) {
	File(root, "baseline.xml").writeText("""
		|<some>
		|	<xml/>
		|</some>
		""".trimMargin())
}
