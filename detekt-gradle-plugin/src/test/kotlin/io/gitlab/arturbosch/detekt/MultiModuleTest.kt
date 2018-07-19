package io.gitlab.arturbosch.detekt

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.io.File

/**
 * @author Markus Schwarz
 */
internal class MultiModuleTest : Spek({

	describe("The Detekt Gradle plugin used in a multi module project") {

		it("is applied to all subprojects individually without sources in root project") {
			val rootDir = createTempDir(prefix = "applyPlugin")
			val projectLayout = ProjectLayout(0)
					.withSubmodule("child1", 2)
					.withSubmodule("child2", 4)

			val detektConfig = ""

			writeFiles(rootDir, detektConfig, projectLayout)
			writeConfig(rootDir)

			// Using a custom "project-cache-dir" to avoid a Gradle error on Windows
			val result = GradleRunner.create()
					.withProjectDir(rootDir)
					.withArguments("--project-cache-dir", createTempDir(prefix = "cache").absolutePath, "detektMain", "--stacktrace", "--info")
					.withPluginClasspath()
					.build()

			assertThat(result.output).contains(projectLayout.submodules.map { "number of classes: ${it.numberOfSourceFiles}" })
			assertThat(result.task(":detektMain")?.outcome).isEqualTo(TaskOutcome.NO_SOURCE)

			assertThat(File(rootDir, "build/reports/detekt/detekt.xml")).doesNotExist()
			assertThat(File(rootDir, "build/reports/detekt/detekt.html")).doesNotExist()
			projectLayout.submodules.forEach {
				assertThat(File(rootDir, "${it.name}/build/reports/detekt/detekt.xml")).exists()
				assertThat(File(rootDir, "${it.name}/build/reports/detekt/detekt.html")).exists()
			}
		}
		it("is applied to all submodules as well as the root project") {
			val rootDir = createTempDir(prefix = "applyPlugin")
			val projectLayout = ProjectLayout(1)
					.withSubmodule("child1", 2)
					.withSubmodule("child2", 4)

			val detektConfig = ""

			writeFiles(rootDir, detektConfig, projectLayout)
			writeConfig(rootDir)

			// Using a custom "project-cache-dir" to avoid a Gradle error on Windows
			val result = GradleRunner.create()
					.withProjectDir(rootDir)
					.withArguments("--project-cache-dir", createTempDir(prefix = "cache").absolutePath, "detektMain", "--stacktrace", "--info")
					.withPluginClasspath()
					.build()

			assertThat(result.output).contains("number of classes: ${projectLayout.numberOfSourceFilesInRoot}")
			assertThat(result.output).contains(projectLayout.submodules.map { "number of classes: ${it.numberOfSourceFiles}" })
			assertThat(result.task(":detektMain")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

			assertThat(File(rootDir, "build/reports/detekt/detekt.xml")).exists()
			assertThat(File(rootDir, "build/reports/detekt/detekt.html")).exists()
			projectLayout.submodules.forEach {
				assertThat(File(rootDir, "${it.name}/build/reports/detekt/detekt.xml")).exists()
				assertThat(File(rootDir, "${it.name}/build/reports/detekt/detekt.html")).exists()
			}
		}
		it("allows to disable report type on root level") {
			val rootDir = createTempDir(prefix = "applyPlugin")
			val projectLayout = ProjectLayout(0)
					.withSubmodule("child1", 2)
					.withSubmodule("child2", 4)

			val detektConfig = """
					|detekt {
					|	reports {
					|		html.enabled = false
					|	}
					|}
				""".trimMargin()

			writeFiles(rootDir, detektConfig, projectLayout)
			writeConfig(rootDir)

			// Using a custom "project-cache-dir" to avoid a Gradle error on Windows
			val result = GradleRunner.create()
					.withProjectDir(rootDir)
					.withArguments("--project-cache-dir", createTempDir(prefix = "cache").absolutePath, "detektMain", "--stacktrace", "--info")
					.withPluginClasspath()
					.build()

			assertThat(result.output).contains(projectLayout.submodules.map { "number of classes: ${it.numberOfSourceFiles}" })
			assertThat(result.task(":detektMain")?.outcome).isEqualTo(TaskOutcome.NO_SOURCE)

			assertThat(File(rootDir, "build/reports/detekt/detekt.xml")).doesNotExist()
			assertThat(File(rootDir, "build/reports/detekt/detekt.html")).doesNotExist()
			projectLayout.submodules.forEach {
				assertThat(File(rootDir, "${it.name}/build/reports/detekt/detekt.xml")).exists()
				assertThat(File(rootDir, "${it.name}/build/reports/detekt/detekt.html")).doesNotExist()
			}
		}
		it("respects properties beeing overwritten in submodules") {
			val rootDir = createTempDir(prefix = "applyPlugin")

			val child1DetektConfig = """
					|detekt {
					|	reports {
					|		html.enabled = true
					|		xml.destination = "build/child1-reports-location/detekt.xml"
					|	}
					|}
				""".trimMargin()

			val projectLayout = ProjectLayout(0)
					.withSubmodule("child1", 2, child1DetektConfig)
					.withSubmodule("child2", 4)

			val detektConfig = """
					|detekt {
					|	reports {
					|		html.enabled = false
					|		xml.destination = "build/custom-reports-location/detekt.xml"
					|	}
					|}
				""".trimMargin()

			writeFiles(rootDir, detektConfig, projectLayout)
			writeConfig(rootDir)

			// Using a custom "project-cache-dir" to avoid a Gradle error on Windows
			val result = GradleRunner.create()
					.withProjectDir(rootDir)
					.withArguments("--project-cache-dir", createTempDir(prefix = "cache").absolutePath, "detektMain", "--stacktrace", "--info")
					.withPluginClasspath()
					.build()

			assertThat(result.output).contains(projectLayout.submodules.map { "number of classes: ${it.numberOfSourceFiles}" })
			assertThat(result.task(":detektMain")?.outcome).isEqualTo(TaskOutcome.NO_SOURCE)

			// Child 1 (html: true, custom xml location)
			assertThat(File(rootDir, "child1/build/child1-reports-location/detekt.xml")).exists()
			assertThat(File(rootDir, "child1/build/reports/detekt/detekt.html")).exists()

			// Child 2 (html: false (per root), custom xml location from root)
			assertThat(File(rootDir, "child2/build/custom-reports-location/detekt.xml")).exists()
			assertThat(File(rootDir, "child2/build/reports/detekt/detekt.html")).doesNotExist()
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
	|allprojects {
	|	repositories {
	|		mavenLocal()
	|		jcenter()
	|	}
	|}
	|
	|subprojects {
	|	apply plugin: "java-library"
	|	apply plugin: "io.gitlab.arturbosch.detekt"
	|}
	|
	|$detektConfiguration
	""".trimMargin()

// src/main/kotlin/MyClass.kt
private fun ktFileContent(className: String) = """
	|class $className
	|
	""".trimMargin()

private fun writeFiles(root: File, detektConfiguration: String, projectLayout: ProjectLayout) {
	File(root, "build.gradle").writeText(buildFileContent(detektConfiguration))
	File(root, "settings.gradle").writeText(settingsContent(projectLayout))
	File(root, "src/main/java").mkdirs()
	(1..projectLayout.numberOfSourceFilesInRoot).forEach {
		val className = "MyRoot${it}Class.kt"
		File(root, "src/main/java/$className").writeText(ktFileContent(className))
	}
	projectLayout.submodules.forEach {
		val submodule = it
		val moduleRoot = File(root, submodule.name)
		File(moduleRoot, "src/main/java").mkdirs()
		File(moduleRoot, "build.gradle").writeText(it.detektConfig ?: "")
		(1..submodule.numberOfSourceFiles).forEach {
			val className = "My${submodule.name}${it}Class.kt"
			File(moduleRoot, "src/main/java/$className").writeText(ktFileContent(className))
		}
	}
}

private fun writeConfig(root: File) {
	File(root, "config.yml").writeText("""
		|autoCorrect: true
		|failFast: false
		""".trimMargin())
}

private fun settingsContent(projectLayout: ProjectLayout) = """
    	| rootProject.name = "root-project"
		| include(${projectLayout.submodules.map { "\"${it.name}\"" }.joinToString(",")})
		|
		""".trimMargin()

private data class Project(val name: String, val numberOfSourceFiles: Int) {
	companion object {
		const val ROOT_PROJECT = "root"
		fun root(numberOfSourceFiles: Int) = Project(ROOT_PROJECT, numberOfSourceFiles)
	}
}

private data class ProjectLayout(val numberOfSourceFilesInRoot: Int, val submodules: List<Submodule> = emptyList()) {
	fun withSubmodule(name: String, numberOfSourceFiles: Int, detektConfig: String? = null) =
			copy(submodules = (submodules + listOf(Submodule(name, numberOfSourceFiles, detektConfig))))
}

private data class Submodule(val name: String, val numberOfSourceFiles: Int, val detektConfig: String? = null)
