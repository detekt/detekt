package dev.detekt.gradle.plugin

import dev.detekt.gradle.plugin.testkit.DslTestBuilder
import dev.detekt.gradle.plugin.testkit.ProjectLayout
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test

class CreateBaselineTaskDslSpec {
    @Test
    fun `detektBaseline task can be executed when baseline file is specified`() {
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
                    numberOfFindingsInRootPerSourceDir = 1,
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
        val detektConfig = """
            detekt {
            }
        """.trimIndent()
        val gradleRunner = DslTestBuilder.kotlin()
            .withProjectLayout(
                ProjectLayout(
                    numberOfSourceFilesInRootPerSourceDir = 1,
                    numberOfFindingsInRootPerSourceDir = 1,
                )
            )
            .withDetektConfig(detektConfig)
            .build()

        gradleRunner.runTasksAndCheckResult("detektBaseline") { result ->
            assertThat(result.task(":detektBaseline")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
            assertThat(projectFile(DEFAULT_BASELINE_FILENAME)).exists()
        }
    }
}

private const val DEFAULT_BASELINE_FILENAME = "detekt-baseline.xml"
