package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.DslTestBuilder.Companion.groovy
import io.gitlab.arturbosch.detekt.DslTestBuilder.Companion.kotlin
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

/**
 * @author Marvin Ramin
 * @author Markus Schwarz
 */
internal class DetektTaskDslTest : Spek({

	describe("When applying the detekt gradle plugin") {
		listOf(groovy(), kotlin()).forEach { builder ->
			describe("using ${builder.gradleBuildName}") {
				it("can be applied without any configuration using its task name") {

					val gradleRunner = builder.build()
					gradleRunner.runDetektTaskAndCheckResult { result ->
						assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
						assertThat(result.output).contains("number of classes: 1")
						assertThat(projectFile("build/reports/detekt/detekt.xml")).exists()
						assertThat(projectFile("build/reports/detekt/detekt.html")).exists()
					}
				}

				it("can be applied without any configuration using the check task") {

					val gradleRunner = builder.build()
					gradleRunner.runTasksAndCheckResult("check") { result ->
						assertThat(result.task(":check")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
						assertThat(result.output).contains("number of classes: 1")
						assertThat(result.output).contains("Ruleset: comments")
					}

				}

				it("can use a custom tool version") {

					val customVersion = "1.0.0.RC8"
					val config = """
						|detekt {
						|	toolVersion = "$customVersion"
						|}
						"""

					val gradleRunner = builder.withDetektConfig(config).build()
					gradleRunner.runTasksAndCheckResult("dependencies", "--configuration", "detekt") { result ->
						assertThat(result.task(":dependencies")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
						assertThat(result.output).contains("io.gitlab.arturbosch.detekt:detekt-cli:$customVersion")
					}
				}

				it("can be applied with multiple config files") {

					val config = """
						|detekt {
						|	config = files("firstConfig.yml", "secondConfig.yml")
						|}
						"""

					val gradleRunner = builder.withDetektConfig(config).build()

					val firstConfig = gradleRunner.projectFile("firstConfig.yml").apply { createNewFile() }
					val secondConfig = gradleRunner.projectFile("secondConfig.yml").apply { createNewFile() }

					gradleRunner.runDetektTaskAndCheckResult { result ->
						assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
						val expectedConfigParam = "--config ${firstConfig.absolutePath},${secondConfig.absolutePath}"
						assertThat(result.output).contains(expectedConfigParam)
					}
				}

				it("can be applied with custom input directories ignoring non existent") {

					val customSrc = "gensrc/kotlin"
					val config = """
						|detekt {
						|	input = files("${customSrc}", "folder_that_does_not_exist")
						|}
						"""

					val gradleRunner = builder
							.withProjectLayout(ProjectLayout(1, srcDirs = listOf(customSrc)))
							.withDetektConfig(config)
							.build()

					gradleRunner.runDetektTaskAndCheckResult() { result ->

						assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
						val expectedInputParam = "--input ${projectFile(customSrc).absolutePath}"
						assertThat(result.output).contains(expectedInputParam)
						assertThat(result.output).doesNotContain("folder_that_does_not_exist")
					}
				}
				it("can be applied with classes in multiple custom input directories") {

					val customSrc1 = "gensrc/kotlin"
					val customSrc2 = "src/main/kotlin"
					val config = """
						|detekt {
						|	input = files("$customSrc1", "$customSrc2")
						|}
						"""

					val projectLayout = ProjectLayout(1, srcDirs = listOf(customSrc1, customSrc2))
					val gradleRunner = builder
							.withProjectLayout(projectLayout)
							.withDetektConfig(config)
							.build()

					gradleRunner.runDetektTaskAndCheckResult() { result ->

						assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
						val expectedInputParam =
								"--input ${projectFile(customSrc1).absolutePath},${projectFile(customSrc2).absolutePath}"
						assertThat(result.output).contains(expectedInputParam)
						assertThat(result.output).contains("number of classes: 2")
					}
				}
				it("can change the general reports dir") {

					val config = """
						|detekt {
						|	reportsDir = file("build/detekt-reports")
						|}
						"""

					val gradleRunner = builder
							.withDetektConfig(config)
							.build()

					gradleRunner.runDetektTaskAndCheckResult() { result ->
						assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
						assertThat(projectFile("build/detekt-reports/detekt.xml")).exists()
						assertThat(projectFile("build/detekt-reports/detekt.html")).exists()
					}
				}
				it("can change the general reports dir but overwrite single report") {

					val config = """
						|detekt {
						|	reportsDir = file("build/detekt-reports")
						|	reports {
						|		xml.destination = file("build/xml-reports/custom-detekt.xml")
						|	}
						|}
						"""

					val gradleRunner = builder
							.withDetektConfig(config)
							.build()

					gradleRunner.runDetektTaskAndCheckResult() { result ->
						assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
						assertThat(projectFile("build/xml-reports/custom-detekt.xml")).exists()
						assertThat(projectFile("build/detekt-reports/detekt.html")).exists()
					}
				}
				it("can disable reports") {

					val config = """
						|detekt {
						|	reports {
						|		xml.enabled = false
						|		html {
						|			enabled = false
						|		}
						|	}
						|}
						"""

					val gradleRunner = builder
							.withDetektConfig(config)
							.build()

					gradleRunner.runDetektTaskAndCheckResult() { result ->
						assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
						assertThat(projectFile("build/reports/detekt/detekt.xml")).doesNotExist()
						assertThat(projectFile("build/reports/detekt/detekt.html")).doesNotExist()
					}
				}
				it("can change all flags") {

					val config = """
						|detekt {
						|	debug = true
						|	parallel = true
						|	disableDefaultRuleSets = true
						|}
						"""

					val gradleRunner = builder
							.withDetektConfig(config)
							.build()

					gradleRunner.runDetektTaskAndCheckResult() { result ->
						assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
						assertThat(result.output).contains("--debug", "--parallel", "--disable-default-rulesets")
					}
				}
				it("can ignore tests by using filters") {

					val config = """
						|detekt {
						|	input = files("${"$"}projectDir")
						|	filters = ".*/test/.*"
						|}
						"""

					val projectLayout = ProjectLayout(1, srcDirs = listOf("src/main/kotlin", "src/test/kotlin"))

					val gradleRunner = builder
							.withDetektConfig(config)
							.withProjectLayout(projectLayout)
							.build()

					gradleRunner.runDetektTaskAndCheckResult() { result ->
						assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
						assertThat(result.output).contains("number of classes: 1")
					}
				}
				it("allows setting a baseline file") {

					val baselineFilename = "detekt-baseline.xml"

					val config = """
						|detekt {
						|	baseline = file("$baselineFilename")
						|}
						"""

					val gradleRunner = builder
							.withDetektConfig(config)
							.withBaseline(baselineFilename)
							.build()

					gradleRunner.runDetektTaskAndCheckResult() { result ->
						assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
						val expectedBaselineArgument = "--baseline ${projectFile(baselineFilename).absolutePath}"
						assertThat(result.output).contains(expectedBaselineArgument)
					}
				}
				it("can be used with formatting plugin") {

					val config = """
					|dependencies {
					| detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:${VERSION_UNDER_TEST}")
					|}
						"""

					val gradleRunner = builder
							.withDetektConfig(config)
							.build()

					gradleRunner.runTasksAndCheckResult("dependencies", "--configuration", "detektPlugins") { result ->
						assertThat(result.task(":dependencies")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
						assertThat(result.output).contains("io.gitlab.arturbosch.detekt:detekt-formatting:${VERSION_UNDER_TEST}")
					}

				}
			}
		}
	}

	describe("Creating tasks for each source set") {
		listOf(groovy(), kotlin()).forEach { builder ->
			describe("using ${builder.gradleBuildName}") {
				it("generates one task for each java source set of the project") {
					val gradleRunner = builder
							.withBaseGradlePlugin(GradlePlugin.JavaLibrary)
							.build()

					gradleRunner.runTasksAndCheckResult("tasks") { result ->
						assertThat(result.output).contains("detektMain")
						assertThat(result.output).contains("detektTest")
					}
				}

				it("can successfully run source set tasks") {
					val gradleRunner = builder
							.withBaseGradlePlugin(GradlePlugin.JavaLibrary)
							.build()

					gradleRunner.writeKtFile("src/main/java", "SomeTestClass")
					gradleRunner.writeKtFile("src/test/java", "SomeTestClass")

					gradleRunner.runTasksAndCheckResult("detektMain", "detektTest") { result ->
						assertThat(result.task(":detektMain")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
						assertThat(result.task(":detektTest")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
					}
				}
				it("respects custom java source sets") {
					val buildGradleKts = """
    					|sourceSets.create("custom")
						""".trimMargin()

					val buildGradle = """
    					|sourceSets.custom
						""".trimMargin()

					val gradleRunner = builder
							.withBuildGradleExtension(buildGradle, buildGradleKts)
							.withBaseGradlePlugin(GradlePlugin.JavaLibrary)
							.build()

					gradleRunner.writeKtFile("src/custom/java", "SomeTestClass")

					gradleRunner.runTasksAndCheckResult("detektCustom") { result ->
						assertThat(result.task(":detektCustom")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
					}
				}

				it("generates a report for a source set task") {
					val gradleRunner = builder
							.withBaseGradlePlugin(GradlePlugin.JavaLibrary)
							.build()

					gradleRunner.writeKtFile("src/main/java", "SomeTestClass")
					gradleRunner.writeKtFile("src/test/java", "SomeTestClass")

					gradleRunner.runTasksAndCheckResult("detektMain") { result ->
						assertThat(projectFile("build/reports/detekt/detektMain.xml")).exists()
						assertThat(projectFile("build/reports/detekt/detektMain.html")).exists()
					}
				}

				it("respects report enable/disable configurations in the source set tasks") {
					val config = """
						|detekt {
						|	reports {
						|		xml.enabled = false
						|		html {
						|			enabled = false
						|		}
						|	}
						|}
						"""

					val gradleRunner = builder
							.withBaseGradlePlugin(GradlePlugin.JavaLibrary)
							.withDetektConfig(config)
							.build()

					gradleRunner.writeKtFile("src/main/java", "SomeTestClass")
					gradleRunner.writeKtFile("src/test/java", "SomeTestClass")

					gradleRunner.runTasksAndCheckResult("detektMain") { result ->
						assertThat(projectFile("build/reports/detekt/detektMain.xml")).doesNotExist()
						assertThat(projectFile("build/reports/detekt/detektMain.html")).doesNotExist()
					}
				}

				it("respects report destination configurations in the source set tasks") {
					val xmlPath = "path/to/xml"
					val htmlPath = "path/to/html"
					val config = """
						|detekt {
						|	reports {
						|		xml.destination = file("$xmlPath/report.xml")
						|		html {
						|			destination = file("$htmlPath/report.html")
						|		}
						|	}
						|}
						"""

					val gradleRunner = builder
							.withBaseGradlePlugin(GradlePlugin.JavaLibrary)
							.withDetektConfig(config)
							.build()

					gradleRunner.writeKtFile("src/main/java", "SomeTestClass")
					gradleRunner.writeKtFile("src/test/java", "SomeTestClass")

					gradleRunner.runTasksAndCheckResult("detektMain") { result ->
						assertThat(projectFile("$xmlPath/detektMain.xml")).exists()
						assertThat(projectFile("$htmlPath/detektMain.html")).exists()
					}
				}

				it("respects reportDir configurations in the source set tasks") {
					val path = "build/detekt-reports"
					val config = """
						|detekt {
						|	reportsDir = file("build/detekt-reports")
						|}
						"""

					val gradleRunner = builder
							.withBaseGradlePlugin(GradlePlugin.JavaLibrary)
							.withDetektConfig(config)
							.build()

					gradleRunner.writeKtFile("src/main/java", "SomeTestClass")
					gradleRunner.writeKtFile("src/test/java", "SomeTestClass")

					gradleRunner.runTasksAndCheckResult("detektMain") { result ->
						assertThat(projectFile("$path/detektMain.xml")).exists()
						assertThat(projectFile("$path/detektMain.html")).exists()
					}
				}

				it("generates kotlin source set tasks") {
					val gradleRunner = builder
							.withBaseGradlePlugin(GradlePlugin.Kotlin)
							.build()

					gradleRunner.writeKtFile("src/main/java", "SomeTestClass")
					gradleRunner.writeKtFile("src/test/java", "SomeTestClass")

					gradleRunner.runTasksAndCheckResult("detektMain", "detektTest") { result ->
						assertThat(result.task(":detektMain")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
						assertThat(result.task(":detektTest")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
					}
				}

				it("generates kotlin-multiplatform source set tasks") {
					val gradleRunner = builder
							.withBaseGradlePlugin(GradlePlugin.KotlinMultiPlatform)
							.build()

					gradleRunner.writeKtFile("src/main/java", "SomeTestClass")
					gradleRunner.writeKtFile("src/test/java", "SomeTestClass")

					gradleRunner.runTasksAndCheckResult("tasks", "detektMain", "detektTest") { result ->
						println(result.output)
						assertThat(result.task(":detektMain")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
						assertThat(result.task(":detektTest")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
					}
				}

				it("generates kotlin2js source set tasks") {
					val gradleRunner = builder
							.withBaseGradlePlugin(GradlePlugin.Kotlin2Js)
							.build()

					gradleRunner.writeKtFile("src/main/java", "SomeTestClass")
					gradleRunner.writeKtFile("src/test/java", "SomeTestClass")

					gradleRunner.runTasksAndCheckResult("tasks", "detektMain", "detektTest") { result ->
						println(result.output)
						assertThat(result.task(":detektMain")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
						assertThat(result.task(":detektTest")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
					}
				}

				it("generates kotlin-android source set tasks") {
					val gradleRunner = builder
							.withBaseGradlePlugin(GradlePlugin.KotlinAndroid)
							.build()

					gradleRunner.writeKtFile("src/main/java", "SomeTestClass")
					gradleRunner.writeKtFile("src/test/java", "SomeTestClass")

					gradleRunner.runTasksAndCheckResult("tasks", "detektMain", "detektTest") { result ->
						println(result.output)
						assertThat(result.task(":detektMain")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
						assertThat(result.task(":detektTest")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
					}
				}
			}
		}

		describe("and creating a custom task") {
			it("can be done using the groovy dsl") {

				val config = """
					|task detektFailFast(type: io.gitlab.arturbosch.detekt.Detekt) {
					|	description = "Runs a failfast detekt build."
					|
					|	input = files("${"$"}projectDir")
					|	config = files("config.yml")
					|	debug = true
					|	parallel = true
					|	disableDefaultRuleSets = true
					|	reports {
					|		xml {
					|			enabled = true
					|			destination = file("build/reports/failfast.xml")
					|		}
					|		html.destination = file("build/reports/failfast.html")
					|	}
					|}
				"""

				val gradleRunner = groovy().withDetektConfig(config).build()
				gradleRunner.writeProjectFile("config.yml", "")

				gradleRunner.runTasksAndCheckResult("detektFailFast") { result ->
					assertThat(result.task(":detektFailFast")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
					assertThat(projectFile("build/reports/failfast.xml")).exists()
					assertThat(projectFile("build/reports/failfast.html")).exists()
				}
			}

			it("can be done using the kotlin dsl") {

				val config = """
					|task<io.gitlab.arturbosch.detekt.Detekt>("detektFailFast") {
					|	description = "Runs a failfast detekt build."
					|
					|	input = files("${"$"}projectDir")
					|	config = files("config.yml")
					|	debug = true
					|	parallel = true
					|	disableDefaultRuleSets = true
					|	reports {
					|		xml {
					|			enabled = true
					|			destination = file("build/reports/failfast.xml")
					|		}
					|		html.destination = file("build/reports/failfast.html")
					|	}
					|}
				"""

				val gradleRunner = kotlin().withDetektConfig(config).build()
				gradleRunner.writeProjectFile("config.yml", "")
				gradleRunner.runTasksAndCheckResult("detektFailFast") { result ->
					assertThat(result.task(":detektFailFast")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
					assertThat(gradleRunner.projectFile("build/reports/failfast.xml")).exists()
					assertThat(gradleRunner.projectFile("build/reports/failfast.html")).exists()
				}
			}
		}
	}
})
