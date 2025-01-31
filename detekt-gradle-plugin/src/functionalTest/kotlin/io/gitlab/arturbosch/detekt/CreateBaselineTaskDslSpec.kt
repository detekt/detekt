package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.testkit.DslTestBuilder
import io.gitlab.arturbosch.detekt.testkit.ProjectLayout
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test

class CreateBaselineTaskDslSpec {
    @Test
    fun `detektBaselineMainSourceSet task can be executed when baseline file is specified`() {
        val baselineFilename = "baseline.xml"

        val detektConfig = """
            detekt {
                baseline = file("$baselineFilename")
            }
        """.trimIndent()
        val gradleRunner = DslTestBuilder.kotlin()
            .withProjectLayout(
                ProjectLayout(
                    numberOfSourceFilesInRootPerSourceDir = 1,
                    numberOfCodeSmellsInRootPerSourceDir = 1,
                )
            )
            .withDetektConfig(detektConfig)
            .build()

        gradleRunner.runTasksAndCheckResult("detektBaselineMainSourceSet") { result ->
            assertThat(result.task(":detektBaselineMainSourceSet")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
            assertThat(projectFile("baseline-mainSourceSet.xml")).exists()
            assertThat(projectFile(DEFAULT_BASELINE_FILENAME)).doesNotExist()
        }
    }

    @Test
    fun `detektBaselineMainSourceSet task can be executed when baseline file is not specified`() {
        val detektConfig = """
            detekt {
            }
        """.trimIndent()
        val gradleRunner = DslTestBuilder.kotlin()
            .withProjectLayout(
                ProjectLayout(
                    numberOfSourceFilesInRootPerSourceDir = 1,
                    numberOfCodeSmellsInRootPerSourceDir = 1,
                )
            )
            .withDetektConfig(detektConfig)
            .build()

        gradleRunner.runTasksAndCheckResult("detektBaselineMainSourceSet") { result ->
            assertThat(result.task(":detektBaselineMainSourceSet")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
            assertThat(projectFile(DEFAULT_BASELINE_FILENAME)).exists()
        }
    }
}

private const val DEFAULT_BASELINE_FILENAME = "detekt-baseline-mainSourceSet.xml"
