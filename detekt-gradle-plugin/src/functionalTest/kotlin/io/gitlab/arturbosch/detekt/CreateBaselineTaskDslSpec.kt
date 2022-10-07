package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.testkit.DslTestBuilder
import io.gitlab.arturbosch.detekt.testkit.ProjectLayout
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test

class CreateBaselineTaskDslSpec {
    @Test
    fun `detektBaseline task can be executed when baseline file is specified`() {
        val baselineFilename = "baseline.xml"

        @Suppress("TrimMultilineRawString")
        val detektConfig = """
            |detekt {
            |   baseline = file("$baselineFilename")
            |}
        """
        val gradleRunner = DslTestBuilder.kotlin()
            .withProjectLayout(
                ProjectLayout(
                    numberOfSourceFilesInRootPerSourceDir = 1,
                    numberOfCodeSmellsInRootPerSourceDir = 1
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

    @Test
    fun `detektBaseline task can be executed when baseline file is not specified`() {
        @Suppress("TrimMultilineRawString")
        val detektConfig = """
            |detekt {
            |}
        """
        val gradleRunner = DslTestBuilder.kotlin()
            .withProjectLayout(
                ProjectLayout(
                    numberOfSourceFilesInRootPerSourceDir = 1,
                    numberOfCodeSmellsInRootPerSourceDir = 1
                )
            )
            .withDetektConfig(detektConfig)
            .build()

        gradleRunner.runTasksAndCheckResult("detektBaseline") { result ->
            assertThat(result.task(":detektBaseline")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
            assertThat(projectFile(DEFAULT_BASELINE_FILENAME)).exists()
        }
    }

    @Test
    fun `detektBaseline task can not be executed when baseline file is specified null`() {
        @Suppress("TrimMultilineRawString")
        val detektConfig = """
            |detekt {
            |   baseline = null
            |}
        """
        val gradleRunner = DslTestBuilder.kotlin()
            .withProjectLayout(
                ProjectLayout(
                    numberOfSourceFilesInRootPerSourceDir = 1,
                    numberOfCodeSmellsInRootPerSourceDir = 1
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

private const val DEFAULT_BASELINE_FILENAME = "detekt-baseline.xml"
