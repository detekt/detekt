package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.testkit.DslTestBuilder
import io.gitlab.arturbosch.detekt.testkit.ProjectLayout
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test

class DetektTaskSpec {

    private val projectLayoutWithTooManyIssues = ProjectLayout(
        numberOfSourceFilesInRootPerSourceDir = 15,
        numberOfCodeSmellsInRootPerSourceDir = 15
    )

    @Test
    fun `build succeeds with more issues than threshold if ignoreFailures = true`() {
        val config = """
            detekt {
                ignoreFailures = true
            }
        """.trimIndent()

        val gradleRunner = DslTestBuilder.kotlin()
            .withProjectLayout(projectLayoutWithTooManyIssues)
            .withDetektConfig(config)
            .build()

        gradleRunner.runDetektTaskAndCheckResult { result ->
            assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        }
    }

    @Test
    fun `build fails with more issues than threshold successfully if ignoreFailures = false`() {
        val config = """
            detekt {
                ignoreFailures = false
            }
        """.trimIndent()

        val gradleRunner = DslTestBuilder.kotlin()
            .withProjectLayout(projectLayoutWithTooManyIssues)
            .withDetektConfig(config)
            .build()

        gradleRunner.runDetektTaskAndExpectFailure { result ->
            assertThat(result.output).contains("Analysis failed with 15 issues.")
        }
    }
}
