package dev.detekt.gradle

import dev.detekt.gradle.testkit.DslTestBuilder
import dev.detekt.gradle.testkit.ProjectLayout
import org.assertj.core.api.Assertions
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test

private const val DEFAULT_BASELINE_FILENAME = "detekt-baseline.xml"

class CreateBaselineTaskDslSpec {
    @Test
    fun `detektBaseline task can be executed when baseline file is specified`() {
        val baselineFilename = "baseline.xml"

        val detektConfig = """
            detekt {
                baseline = file("$baselineFilename")
            }
        """.trimIndent()
        val gradleRunner = DslTestBuilder.Companion.kotlin()
            .withProjectLayout(
                ProjectLayout(
                    numberOfSourceFilesInRootPerSourceDir = 1,
                    numberOfFindingsInRootPerSourceDir = 1,
                )
            )
            .withDetektConfig(detektConfig)
            .build()

        gradleRunner.runTasksAndCheckResult("detektBaseline") { result ->
            Assertions.assertThat(result.task(":detektBaseline")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
            Assertions.assertThat(projectFile(baselineFilename)).exists()
            Assertions.assertThat(projectFile(DEFAULT_BASELINE_FILENAME)).doesNotExist()
        }
    }

    @Test
    fun `detektBaseline task can be executed when baseline file is not specified`() {
        val detektConfig = """
            detekt {
            }
        """.trimIndent()
        val gradleRunner = DslTestBuilder.Companion.kotlin()
            .withProjectLayout(
                ProjectLayout(
                    numberOfSourceFilesInRootPerSourceDir = 1,
                    numberOfFindingsInRootPerSourceDir = 1,
                )
            )
            .withDetektConfig(detektConfig)
            .build()

        gradleRunner.runTasksAndCheckResult("detektBaseline") { result ->
            Assertions.assertThat(result.task(":detektBaseline")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
            Assertions.assertThat(projectFile(DEFAULT_BASELINE_FILENAME)).exists()
        }
    }
}
