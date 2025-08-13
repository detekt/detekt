package dev.detekt.gradle

import dev.detekt.gradle.testkit.DslTestBuilder
import dev.detekt.gradle.testkit.ProjectLayout
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test

class DetektTaskSpec {

    private val projectLayoutWithIssues = ProjectLayout(
        numberOfSourceFilesInRootPerSourceDir = 1,
        numberOfFindingsInRootPerSourceDir = 1
    )

    @Test
    fun `build succeeds with issues if ignoreFailures = true`() {
        val config = """
            detekt {
                ignoreFailures = true
            }
        """.trimIndent()

        val gradleRunner = DslTestBuilder.kotlin()
            .withProjectLayout(projectLayoutWithIssues)
            .withDetektConfig(config)
            .build()

        gradleRunner.runDetektTaskAndCheckResult { result ->
            assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        }
    }

    @Test
    fun `build succeeds with issues if failOnSeverity = never`() {
        val config = """
            detekt {
                failOnSeverity = dev.detekt.gradle.extensions.FailOnSeverity.Never
            }
        """.trimIndent()

        val gradleRunner = DslTestBuilder.kotlin()
            .withProjectLayout(projectLayoutWithIssues)
            .withDetektConfig(config)
            .build()

        gradleRunner.runDetektTaskAndCheckResult { result ->
            assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        }
    }

    @Test
    fun `build fails with issues if ignoreFailures = false`() {
        val config = """
            detekt {
                ignoreFailures = false
            }
        """.trimIndent()

        val gradleRunner = DslTestBuilder.kotlin()
            .withProjectLayout(projectLayoutWithIssues)
            .withDetektConfig(config)
            .build()

        gradleRunner.runDetektTaskAndExpectFailure { result ->
            assertThat(result.output).contains("Analysis failed with 1 issues.")
        }
    }

    @Test
    fun `build fails with issues if failOnSeverity = error`() {
        val config = """
            detekt {
                failOnSeverity = dev.detekt.gradle.extensions.FailOnSeverity.Error
            }
        """.trimIndent()

        val gradleRunner = DslTestBuilder.kotlin()
            .withProjectLayout(projectLayoutWithIssues)
            .withDetektConfig(config)
            .build()

        gradleRunner.runDetektTaskAndExpectFailure { result ->
            assertThat(result.output).contains("Analysis failed with 1 issues.")
        }
    }
}
