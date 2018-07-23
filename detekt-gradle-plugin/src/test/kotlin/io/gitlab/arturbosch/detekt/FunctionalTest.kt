package io.gitlab.arturbosch.detekt

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import java.io.File

/**
 * @author Olivier Lemasle
 */
internal class FunctionalTest : Spek({

	describe("The Detekt Gradle plugin") {

		it("uses built-in rules") {
			val rootDir = createTempDir(prefix = "withoutCustomRules")
			writeFiles(rootDir)

			val result = GradleRunner.create()
				.withProjectDir(rootDir)
				.withArguments("detektCheck")
				.withPluginClasspath()
				.build()

			assertThat(result.output).contains("number of classes: 1")
			assertThat(result.output).contains("Ruleset: comments")
			assertThat(result.task(":detektCheck")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

			// Asserts that the "custom" module is not built, and that custom ruleset is not enabled
			assertThat(result.output).doesNotContain("Ruleset: test-custom")
			assertThat(File(rootDir, "custom/build")).doesNotExist()
		}

		it("can use custom rules from a project's module") {
			val rootDir = createTempDir(prefix = "withCustomRules")
			writeFiles(rootDir)

			File(rootDir, "build.gradle").appendText(
				"""
				|dependencies {
				|   detekt project(':custom')
				|}
				""".trimMargin()
			)

			val result = GradleRunner.create()
				.withProjectDir(rootDir)
				.withArguments("detektCheck")
				.withPluginClasspath()
				.build()

			assertThat(result.output).contains("number of classes: 1")
			assertThat(result.output).contains("Ruleset: comments")
			assertThat(result.task(":detektCheck")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

			// Asserts that the "custom" module is built, and that custom ruleset is enabled
			assertThat(result.output).contains("Ruleset: test-custom")
			assertThat(File(rootDir, "custom/build")).exists()
		}
	}
})

// build.gradle
private val buildFileContent = """
	|plugins {
	|   id 'io.gitlab.arturbosch.detekt'
	|}
	|repositories {
	|   jcenter()
	|}
	|detekt {
	|   profile('main') {
	|      input = "${"$"}projectDir/src/main/kotlin"
	|   }
	|}
	|
	""".trimMargin()

// settings.gradle
private const val settingsFileContent = """include ":custom""""

// src/main/kotlin/MyClass.kt
private val ktFileContent = """
	|class MyClass
	|
	""".trimMargin()

// custom/build.gradle
private val customBuildFileContent = """
	|plugins {
	|   id "org.jetbrains.kotlin.jvm" version "1.2.41"
	|}
	|repositories {
	|   jcenter()
	|}
	|dependencies {
	|   implementation "io.gitlab.arturbosch.detekt:detekt-api:1.0.0.RC7"
	|}
	""".trimMargin()

// custom/src/main/kotlin/RulesProvider.kt
private val customRulesProviderContent = """
	|import io.gitlab.arturbosch.detekt.api.Config
	|import io.gitlab.arturbosch.detekt.api.RuleSet
	|import io.gitlab.arturbosch.detekt.api.RuleSetProvider
	|
	|class RulesProvider : RuleSetProvider {
	|   override val ruleSetId: String = "test-custom"
	|   override fun instance(config: Config) = RuleSet(ruleSetId, listOf())
	|}
	|
	""".trimMargin()

// custom/src/main/resources/META-INF/services/io.gitlab.arturbosch.detekt.api.RuleSetProvider
private const val ruleSetProviderSpiContent = "RulesProvider"

private fun writeFiles(root: File) {
	File(root, "build.gradle").writeText(buildFileContent)
	File(root, "settings.gradle").writeText(settingsFileContent)
	File(root, "src/main/kotlin").mkdirs()
	File(root, "src/main/kotlin/MyClass.kt").writeText(ktFileContent)
	File(root, "custom").mkdirs()
	File(root, "custom/build.gradle").writeText(customBuildFileContent)
	File(root, "custom/src/main/kotlin").mkdirs()
	File(root, "custom/src/main/kotlin/RulesProvider.kt").writeText(customRulesProviderContent)
	File(root, "custom/src/main/resources/META-INF/services").mkdirs()
	File(root, "custom/src/main/resources/META-INF/services/io.gitlab.arturbosch.detekt.api.RuleSetProvider")
		.writeText(ruleSetProviderSpiContent)
}
