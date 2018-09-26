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

	val buildGradle = """
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
		""".trimMargin()
	val dslTest = DslBaseTest("build.gradle.kts", buildGradle)

	describe("The Detekt Gradle plugin used in a build.gradle.kts file") {
		lateinit var rootDir: File
		beforeEachTest {
			rootDir = createTempDir(prefix = "applyPlugin")
		}
		it("can be used without configuration") {

			val detektConfig = ""

			dslTest.writeFiles(rootDir, detektConfig)
			dslTest.writeConfig(rootDir)

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

			val srcDir = File(rootDir, "src/main/kotlin")
			dslTest.writeFiles(rootDir, detektConfig, srcDir)
			dslTest.writeConfig(rootDir)

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

			dslTest.writeFiles(rootDir, detektConfig)
			dslTest.writeConfig(rootDir)

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
			val configPath = File(rootDir, "config.yml")

			val detektConfig = """
					|detekt {
					|	config = files("${configPath.safeAbsolutePath}")
					|}
				"""

			dslTest.writeFiles(rootDir, detektConfig)
			dslTest.writeConfig(rootDir)

			// Using a custom "project-cache-dir" to avoid a Gradle error on Windows
			val result = GradleRunner.create()
					.withProjectDir(rootDir)
					.withArguments("--project-cache-dir", createTempDir(prefix = "cache").absolutePath, "check", "--stacktrace", "--info")
					.withPluginClasspath()
					.build()

			assertThat(result.output).contains("Ruleset: comments", "number of classes: 1", "--config ${configPath.absolutePath}")
			assertThat(result.task(":check")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
		}

		it("can be applied with a full config") {
			val configFile = File(rootDir, "config.yml")
			val baselineFile = File(rootDir, "baseline.xml")

			val detektConfig = """
					|detekt {
					|	debug = true
					|	parallel = true
					|	disableDefaultRuleSets = true
					|	toolVersion = "$VERSION_UNDER_TEST"
					|	config = files("${configFile.safeAbsolutePath}")
					|	baseline = file("${baselineFile.safeAbsolutePath}")
					|	filters = ".*/resources/.*, .*/build/.*"
					|}
				"""

			dslTest.writeFiles(rootDir, detektConfig)
			dslTest.writeConfig(rootDir)
			dslTest.writeBaseline(rootDir)

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
			val configFile = File(rootDir, "config.yml")

			val detektConfig = """
					|task<io.gitlab.arturbosch.detekt.Detekt>("detektFailFast") {
					|	description = "Runs a failfast detekt build."
					|
					|	input = files("src/main/java")
					|	config = files("${configFile.safeAbsolutePath}")
					|	debug = true
					|	reports {
					|		xml {
					|			destination = file("build/reports/failfast.xml")
					|		}
					|		html.destination = file("build/reports/failfast.html")
					|	}
					|}
				"""

			dslTest.writeFiles(rootDir, detektConfig)
			dslTest.writeConfig(rootDir, failfast = true)
			dslTest.writeBaseline(rootDir)

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

			dslTest.writeFiles(rootDir, detektConfig)
			dslTest.writeConfig(rootDir)
			dslTest.writeBaseline(rootDir)

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

			dslTest.writeFiles(rootDir, detektConfig)
			dslTest.writeConfig(rootDir)
			dslTest.writeBaseline(rootDir)

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
			val customSourceLocation = File(rootDir, "gensrc/kotlin")
			val detektConfig = """
					|detekt {
					| debug = true
					| input = files("${customSourceLocation.safeAbsolutePath}")
					|}
				"""

			dslTest.writeFiles(rootDir, detektConfig, customSourceLocation)
			dslTest.writeConfig(rootDir)
			dslTest.writeBaseline(rootDir)

			// Using a custom "project-cache-dir" to avoid a Gradle error on Windows
			val result = GradleRunner.create()
					.withProjectDir(rootDir)
					.withArguments("--project-cache-dir", createTempDir(prefix = "cache").absolutePath, "check", "--stacktrace", "--info")
					.withPluginClasspath()
					.build()

			assertThat(result.output).contains("number of classes: 1")
			assertThat(result.output).contains("--input, ${customSourceLocation.absolutePath}")
			assertThat(result.task(":check")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
		}

		it("can be used without configuration") {
			val detektConfig = """
					|dependencies {
					| detekt("io.gitlab.arturbosch.detekt:detekt-formatting:${VERSION_UNDER_TEST}")
					|}
				"""

			dslTest.writeFiles(rootDir, detektConfig)
			dslTest.writeConfig(rootDir)

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
	}
})

