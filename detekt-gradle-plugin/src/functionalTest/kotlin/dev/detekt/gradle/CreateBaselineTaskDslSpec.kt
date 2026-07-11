package dev.detekt.gradle

import dev.detekt.gradle.testkit.DslTestBuilder
import dev.detekt.gradle.testkit.ProjectLayout
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
            .withConfigFile("config/detekt/detekt.yml")
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
            .withConfigFile("config/detekt/detekt.yml")
            .build()

        gradleRunner.runTasksAndCheckResult("detektBaseline") { result ->
            assertThat(result.task(":detektBaseline")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
            assertThat(projectFile(DEFAULT_BASELINE_FILENAME)).exists()
        }
    }

    @Test
    fun `detektBaseline task creates hash-addressed baseline fragments`() {
        val directoryName = "detekt-baseline.d"
        val detektConfig = """
            detekt {
                baselineFragments.set(layout.projectDirectory.dir("$directoryName"))
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
            .withConfigFile("config/detekt/detekt.yml")
            .build()

        gradleRunner.runTasksAndCheckResult("detektBaseline") { result ->
            assertThat(result.task(":detektBaseline")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
            assertThat(projectFile(DEFAULT_BASELINE_FILENAME)).doesNotExist()
            assertThat(projectFile(directoryName).walkTopDown().filter { it.extension == "xml" }.toList()).isNotEmpty()
        }

        gradleRunner.runTasksAndCheckResult("detekt") { result ->
            assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        }

        gradleRunner.projectFile(DEFAULT_BASELINE_FILENAME).writeText("unused baseline artifact")

        gradleRunner.runTasksAndCheckResult("detekt") { result ->
            assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.UP_TO_DATE)
        }
    }

    @Test
    fun `detekt task ignores a missing baseline fragments directory`() {
        val detektConfig = """
            detekt {
                baselineFragments.set(layout.projectDirectory.dir("missing-baseline.d"))
            }
        """.trimIndent()
        val gradleRunner = DslTestBuilder.kotlin()
            .withDetektConfig(detektConfig)
            .build()

        gradleRunner.runTasksAndCheckResult("detekt") { result ->
            assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        }
    }

    @Test
    fun `detektBaseline task gives fragments precedence over an explicitly configured baseline`() {
        val baselineFilename = "unused-baseline.xml"
        val directoryName = "detekt-baseline.d"
        val detektConfig = """
            detekt {
                baseline = file("$baselineFilename")
                baselineFragments.set(layout.projectDirectory.dir("$directoryName"))
            }
        """.trimIndent()
        val gradleRunner = DslTestBuilder.kotlin()
            .withDetektConfig(detektConfig)
            .withProjectLayout(
                ProjectLayout(
                    numberOfSourceFilesInRootPerSourceDir = 1,
                    numberOfFindingsInRootPerSourceDir = 1,
                )
            )
            .withConfigFile("config/detekt/detekt.yml")
            .build()

        gradleRunner.runTasksAndCheckResult("detektBaseline") { result ->
            assertThat(result.task(":detektBaseline")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
            assertThat(projectFile(baselineFilename)).doesNotExist()
            assertThat(projectFile(directoryName).walkTopDown().filter { it.extension == "xml" }.toList()).isNotEmpty()
        }
    }
}

private const val DEFAULT_BASELINE_FILENAME = "detekt-baseline.xml"
