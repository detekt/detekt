package io.gitlab.arturbosch.detekt

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.io.File

class GenerateConfigTaskGroovyTest : Spek({

	describe("The generate config task of the Detekt Gradle plugin") {
		lateinit var rootDir: File
		beforeEachTest {
			rootDir = createTempDir(prefix = "applyPlugin")
		}
		it("can be executed without any configuration") {

			val detektConfig = """
				"""

			writeFiles(rootDir, detektConfig)

			// Using a custom "project-cache-dir" to avoid a Gradle error on Windows
			val result = GradleRunner.create()
					.withProjectDir(rootDir)
					.withArguments("--project-cache-dir", createTempDir(prefix = "cache").absolutePath, "detektGenerateConfig", "--stacktrace", "--info")
					.withPluginClasspath()
					.build()

			assertThat(result.task(":detektGenerateConfig")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
			assertThat(File(rootDir, "default-detekt-config.yml")).exists()
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

private val ktFileContent = """
	|class MyClass
	|
	""".trimMargin()

private fun writeFiles(root: File, detektConfiguration: String) {
	File(root, "build.gradle").writeText(buildFileContent(detektConfiguration))
	File(root, "settings.gradle").writeText("")
	File(root, "src/main/java").mkdirs()
	File(root, "src/main/java/MyClass.kt").writeText(ktFileContent)
}

