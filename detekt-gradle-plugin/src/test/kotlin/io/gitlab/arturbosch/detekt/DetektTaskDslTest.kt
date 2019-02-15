package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.DslTestBuilder.Companion.groovy
import io.gitlab.arturbosch.detekt.DslTestBuilder.Companion.kotlin
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

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
						|	input = files("$customSrc", "folder_that_does_not_exist")
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
						|	input = files("${"$"}projectDir/src")
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
					| detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$VERSION_UNDER_TEST")
					|}
						"""

                    val gradleRunner = builder
                            .withDetektConfig(config)
                            .build()

                    gradleRunner.runTasksAndCheckResult("dependencies", "--configuration", "detektPlugins") { result ->
                        assertThat(result.task(":dependencies")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                        assertThat(result.output).contains("io.gitlab.arturbosch.detekt:detekt-formatting:$VERSION_UNDER_TEST")
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
					|	input = files("${"$"}projectDir/src")
					|	config = files("config.yml")
					|	debug = true
					|	parallel = true
					|	disableDefaultRuleSets = true
					|	buildUponDefaultConfig = true
					|	failFast = false
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
					|	input = files("${"$"}projectDir/src")
					|	config = files("config.yml")
					|	debug = true
					|	parallel = true
					|	disableDefaultRuleSets = true
					|	buildUponDefaultConfig = true
					|	failFast = false
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
