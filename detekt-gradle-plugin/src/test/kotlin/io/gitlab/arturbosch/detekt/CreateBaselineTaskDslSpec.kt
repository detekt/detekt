package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.testkit.DslTestBuilder
import io.gitlab.arturbosch.detekt.testkit.ProjectLayout
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

internal class CreateBaselineTaskDslSpec : Spek({
    describe("The detektBaseline task of the Detekt Gradle plugin") {
        listOf(DslTestBuilder.groovy(), DslTestBuilder.kotlin()).forEach { builder ->
            describe("using ${builder.gradleBuildName}") {
                it("can be executed when baseline file is specified") {
                    val baselineFilename = "baseline.xml"

                    val detektConfig = """
                        |detekt {
                        |   baseline = file("$baselineFilename")
                        |}
                        """
                    val gradleRunner = builder
                        .withProjectLayout(
                            ProjectLayout(
                                numberOfSourceFilesInRootPerSourceDir = 1,
                                numberOfCodeSmellsInRootPerSourceDir = 1,
                            )
                        )
                        .withDetektConfig(detektConfig)
                        .build()

                    gradleRunner.runTasksAndCheckResult("detektBaseline") { result ->
                        assertThat(result.task(":detektBaseline")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                        assertThat(projectFile(baselineFilename)).exists()
                        assertThat(projectFile(DEFAULT_BASELINE_FILENAME)).doesNotExist()
                    }
                }

                it("can be executed when baseline file is not specified") {
                    val detektConfig = """
                        |detekt {
                        |}
                        """
                    val gradleRunner = builder
                        .withProjectLayout(
                            ProjectLayout(
                                numberOfSourceFilesInRootPerSourceDir = 1,
                                numberOfCodeSmellsInRootPerSourceDir = 1,
                            )
                        )
                        .withDetektConfig(detektConfig)
                        .build()

                    gradleRunner.runTasksAndCheckResult("detektBaseline") { result ->
                        assertThat(result.task(":detektBaseline")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                        assertThat(projectFile(DEFAULT_BASELINE_FILENAME)).exists()
                    }
                }

                it("can not be executed when baseline file is specified null") {
                    val detektConfig = """
                        |detekt {
                        |   baseline = null
                        |}
                        """
                    val gradleRunner = builder
                        .withProjectLayout(
                            ProjectLayout(
                                numberOfSourceFilesInRootPerSourceDir = 1,
                                numberOfCodeSmellsInRootPerSourceDir = 1,
                            )
                        )
                        .withDetektConfig(detektConfig)
                        .build()

                    gradleRunner.runTasksAndExpectFailure("detektBaseline") { result ->
                        assertThat(result.output).contains("property 'baseline' doesn't have a configured value")
                        assertThat(projectFile(DEFAULT_BASELINE_FILENAME)).doesNotExist()
                    }
                }
            }
        }
    }
})

private const val DEFAULT_BASELINE_FILENAME = "detekt-baseline.xml"
