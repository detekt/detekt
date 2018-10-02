package io.gitlab.arturbosch.detekt

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.io.File

/**
 * Tests that run the Detekt Gradle Plugins tasks multiple times to check for correct
 * UP-TO-DATE states and correct build caching.
 */
internal class PluginTaskBehaviorTest : Spek({

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

	describe("The Detekt Gradle Plugin :detekt Task") {
		lateinit var rootDir: File
		lateinit var configFile: File
		lateinit var baselineFile: File
		beforeEachTest {
			rootDir = createTempDir(prefix = "applyPlugin")
			configFile = File(rootDir, "config.yml")
			baselineFile = File(rootDir, "baseline.xml")

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
		}

		it("should be UP-TO-DATE the 2nd run without changes") {
			val gradleRunner = GradleRunner.create()
					.withProjectDir(rootDir)
					.withPluginClasspath()

			// Using a custom "project-cache-dir" to avoid a Gradle error on Windows
			val projectCacheDir = createTempDir(prefix = "cache").absolutePath

			val result = gradleRunner
					.withArguments("--project-cache-dir", projectCacheDir, "detekt", "--stacktrace", "--info")
					.build()

			assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

			// Running the same task again should be UP-TO-DATE
			val secondResult = gradleRunner
					.withArguments("--project-cache-dir", projectCacheDir, "detekt", "--stacktrace", "--info")
					.build()

			assertThat(secondResult.task(":detekt")?.outcome).isEqualTo(TaskOutcome.UP_TO_DATE)
		}

		it("should pick up build artifacts from the build cache on a 2nd run after deleting the build/ dir") {
			val gradleRunner = GradleRunner.create()
					.withTestKitDir(createTempDir())
					.withProjectDir(rootDir)
					.withPluginClasspath()

			// Using a custom "project-cache-dir" to avoid a Gradle error on Windows
			val projectCacheDir = createTempDir(prefix = "cache").absolutePath

			val result = gradleRunner
					.withArguments("--build-cache", "--project-cache-dir", projectCacheDir, "detekt", "--stacktrace", "--info")
					.build()

			assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

			// Delete the "build" directory so that no local artifacts remain
			File(rootDir, "build").deleteRecursively()
			// Running detekt again should pick up artifacts from Build Cache
			val secondResult = gradleRunner
					.withArguments("--build-cache", "--project-cache-dir", projectCacheDir, "detekt", "--stacktrace", "--info")
					.build()

			assertThat(secondResult.task(":detekt")?.outcome).isEqualTo(TaskOutcome.FROM_CACHE)
		}

		it("should pick up build artifacts from the build cache on a 2nd run after running :clean") {
			val gradleRunner = GradleRunner.create()
					.withTestKitDir(createTempDir())
					.withProjectDir(rootDir)
					.withPluginClasspath()

			// Using a custom "project-cache-dir" to avoid a Gradle error on Windows
			val projectCacheDir = createTempDir(prefix = "cache").absolutePath

			val result = gradleRunner
					.withArguments("--build-cache", "--project-cache-dir", projectCacheDir, "detekt", "--stacktrace", "--info")
					.build()

			assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

			// Run a clean
			val cleanResult = gradleRunner
					.withArguments("--build-cache", "--project-cache-dir", projectCacheDir, "clean", "--stacktrace", "--info")
					.build()

			assertThat(cleanResult.task(":clean")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

			// Running detekt again should pick up artifacts from Build Cache
			val secondResult = gradleRunner
					.withArguments("--build-cache", "--project-cache-dir", projectCacheDir, "detekt", "--stacktrace", "--info")
					.build()

			assertThat(secondResult.task(":detekt")?.outcome).isEqualTo(TaskOutcome.FROM_CACHE)
		}

		it("should run again after changing config") {
			val gradleRunner = GradleRunner.create()
					.withProjectDir(rootDir)
					.withPluginClasspath()

			// Using a custom "project-cache-dir" to avoid a Gradle error on Windows
			val projectCacheDir = createTempDir(prefix = "cache").absolutePath

			val result = gradleRunner
					.withArguments("--project-cache-dir", projectCacheDir, "detekt", "--stacktrace", "--info")
					.build()

			assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

			// Change Config file
			dslTest.writeConfig(rootDir, true)

			// Running the same task again should NOT be UP-TO-DATE
			val secondResult = gradleRunner
					.withArguments("--project-cache-dir", projectCacheDir, "detekt", "--stacktrace", "--info")
					.build()

			assertThat(secondResult.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
		}

		it("should run again after changing baseline") {
			val gradleRunner = GradleRunner.create()
					.withProjectDir(rootDir)
					.withPluginClasspath()

			// Using a custom "project-cache-dir" to avoid a Gradle error on Windows
			val projectCacheDir = createTempDir(prefix = "cache").absolutePath

			val result = gradleRunner
					.withArguments("--project-cache-dir", projectCacheDir, "detekt", "--stacktrace", "--info")
					.build()

			assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

			// Change Config file
			baselineFile.writeText("""
							|<some>
							|	<more/>
							|	<xml/>
							|</some>
							""".trimMargin())

			// Running the same task again should NOT be UP-TO-DATE
			val secondResult = gradleRunner
					.withArguments("--project-cache-dir", projectCacheDir, "detekt", "--stacktrace", "--info")
					.build()

			assertThat(secondResult.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
		}

		it("should run again after changing inputs") {
			val gradleRunner = GradleRunner.create()
					.withProjectDir(rootDir)
					.withPluginClasspath()

			// Using a custom "project-cache-dir" to avoid a Gradle error on Windows
			val projectCacheDir = createTempDir(prefix = "cache").absolutePath

			val result = gradleRunner
					.withArguments("--project-cache-dir", projectCacheDir, "detekt", "--stacktrace", "--info")
					.build()

			assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

			dslTest.writeSourceFile(rootDir, filename = "OtherFile.kt")

			// Running the same task again should NOT be UP-TO-DATE
			val secondResult = gradleRunner
					.withArguments("--project-cache-dir", projectCacheDir, "detekt", "--stacktrace", "--info")
					.build()

			assertThat(secondResult.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
		}
	}
})
