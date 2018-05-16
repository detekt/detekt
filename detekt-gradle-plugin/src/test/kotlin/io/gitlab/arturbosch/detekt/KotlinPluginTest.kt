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
internal class KotlinPluginTest : Spek({

	describe("The Detekt Gradle plugin used in a build.gradle.kts file") {

		it("can be applied") {
			val rootDir = createTempDir(prefix = "applyPlugin")
			writeFiles(rootDir)

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
	}
})

// build.gradle.kts
private val buildFileContent = """
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
	|detekt {
	|	toolVersion = "1.0.0.RC6-MARVIN2"
	|}
	""".trimMargin()

//	|configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
//	|	toolVersion = "1.0.0.RC6-4"
//	|}

// src/main/kotlin/MyClass.kt
private val ktFileContent = """
	|class MyClass
	|
	""".trimMargin()

private fun writeFiles(root: File) {
	File(root, "build.gradle.kts").writeText(buildFileContent)
	File(root, "src/main/java").mkdirs()
	File(root, "src/main/java/MyClass.kt").writeText(ktFileContent)
}
