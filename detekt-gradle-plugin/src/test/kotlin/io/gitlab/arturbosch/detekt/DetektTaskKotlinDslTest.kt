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
internal class DetektTaskKotlinDslTest : Spek({

	describe("The Detekt Gradle plugin used in a build.gradle.kts file") {
		lateinit var rootDir: File
		beforeEachTest {
			rootDir = createTempDir(prefix = "applyPlugin")
		}
		it("can be used without configuration") {

			val detektConfig = ""

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
		}

		it("can be used without configuration using source files in src/main/kotlin") {

			val detektConfig = ""

			writeFiles(rootDir, detektConfig, "src/main/kotlin")
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
		}

		it("can be applied with a custom detekt version") {

			val detektConfig = """
					|detekt {
					|	toolVersion = "$VERSION_UNDER_TEST"
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

			val detektConfig = """
					|detekt {
					|	config = files("${rootDir.absolutePath}/config.yml")
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

			assertThat(result.output).contains("Ruleset: comments", "number of classes: 1", "--config ${rootDir.absolutePath}/config.yml")
			assertThat(result.task(":check")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
		}

		it("can be applied with a full config") {


			val detektConfig = """
					|detekt {
					|	debug = true
					|	parallel = true
					|	disableDefaultRuleSets = true
					|	toolVersion = "$VERSION_UNDER_TEST"
					|	config = files("${rootDir.absolutePath}/config.yml")
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
			assertThat(result.output).contains("--parallel", "--debug", "--disable-default-rulesets")
			assertThat(result.output).doesNotContain("Ruleset: comments")
			assertThat(result.task(":check")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
		}

		it("can configure a new custom detekt task") {

			val detektConfig = """
					|task<io.gitlab.arturbosch.detekt.Detekt>("detektFailFast") {
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
			writeConfig(rootDir, failfast = true)
			writeBaseline(rootDir)

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

		it("can configure reports") {

			val detektConfig = """
					|detekt {
					|	reports {
					|		xml.destination = file("build/xml/detekt.xml")
					|		html.destination = file("build/html/detekt.html")
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

			assertThat(File(rootDir, "build/xml/detekt.xml")).exists()
			assertThat(File(rootDir, "build/html/detekt.html")).exists()
		}

		it("can configure reports base directory") {
			val detektConfig = """
					|detekt {
					|	reportsDir = file("build/my-reports")
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
			assertThat(result.task(":check")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

			assertThat(File(rootDir, "build/my-reports/detekt.xml")).exists()
			assertThat(File(rootDir, "build/my-reports/detekt.html")).exists()
		}

		it("can configure the input directory") {
			val customSourceLocation = "gensrc/kotlin"
			val detektConfig = """
					|detekt {
					| debug = true
					| input = files("$customSourceLocation")
					|}
				"""

			writeFiles(rootDir, detektConfig, customSourceLocation)
			writeConfig(rootDir)
			writeBaseline(rootDir)

			// Using a custom "project-cache-dir" to avoid a Gradle error on Windows
			val result = GradleRunner.create()
					.withProjectDir(rootDir)
					.withArguments("--project-cache-dir", createTempDir(prefix = "cache").absolutePath, "check", "--stacktrace", "--info")
					.withPluginClasspath()
					.build()

			assertThat(result.output).contains("number of classes: 1")
			assertThat(result.output).contains("--input, $rootDir/$customSourceLocation")
			assertThat(result.task(":check")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
		}

		it("can add an additional detekt dependency") {
			val customSourceLocation = "gensrc/kotlin"
			val detektConfig = """
					|detekt {
					| debug = true
					|}
					|
					|dependencies {
					| detekt("io.gitlab.arturbosch.detekt:detekt-formatting:${VERSION_UNDER_TEST}")
					|}
				"""

			writeFiles(rootDir, detektConfig, customSourceLocation)
			writeConfig(rootDir)
			writeBaseline(rootDir)

			// Using a custom "project-cache-dir" to avoid a Gradle error on Windows
			val result = GradleRunner.create()
					.withProjectDir(rootDir)
					.withArguments("--project-cache-dir", createTempDir(prefix = "cache").absolutePath, "check", "--stacktrace", "--info")
					.withPluginClasspath()
					.build()

			assertThat(result.task(":check")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
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
	|internal class MyClass
	|
	""".trimMargin()

private fun writeFiles(root: File, detektConfig: String, sourceDir: String = "src/main/java") {
	File(root, "build.gradle.kts").writeText(getBuildFileContent(detektConfig))
	File(root, sourceDir).mkdirs()
	File(root, "$sourceDir/MyClass.kt").writeText(ktFileContent)
}

private fun writeConfig(root: File, failfast: Boolean = false) {
	File(root, "config.yml").writeText("""
		|autoCorrect: true
		|failFast: $failfast
		""".trimMargin())
}

private fun writeBaseline(root: File) {
	File(root, "baseline.xml").writeText("""
		|<some>
		|	<xml/>
		|</some>
		""".trimMargin())
}
