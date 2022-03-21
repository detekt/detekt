package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.testkit.DslTestBuilder
import io.gitlab.arturbosch.detekt.testkit.ProjectLayout
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class DetektTaskSpec {

    private val projectLayoutWithTooManyIssues = ProjectLayout(
        numberOfSourceFilesInRootPerSourceDir = 15,
        numberOfCodeSmellsInRootPerSourceDir = 15
    )

    @ParameterizedTest(name = "Using {0}, build succeeds with more issues than threshold if ignoreFailures = true")
    @MethodSource("io.gitlab.arturbosch.detekt.testkit.DslTestBuilder#builders")
    fun ignoreFailures(builder: DslTestBuilder) {
        val config = """
            |detekt {
            |   ignoreFailures = true
            |}
        """

        val gradleRunner = builder
            .withProjectLayout(projectLayoutWithTooManyIssues)
            .withDetektConfig(config)
            .build()

        gradleRunner.runDetektTaskAndCheckResult { result ->
            assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        }
    }

    @ParameterizedTest(name = "Using {0}, build fails with more issues than threshold successfully if ignoreFailures = false")
    @MethodSource("io.gitlab.arturbosch.detekt.testkit.DslTestBuilder#builders")
    fun doNotIgnoreFailures(builder: DslTestBuilder) {
        val config = """
            |detekt {
            |   ignoreFailures = false
            |}
        """

        val gradleRunner = builder
            .withProjectLayout(projectLayoutWithTooManyIssues)
            .withDetektConfig(config)
            .build()

        gradleRunner.runDetektTaskAndExpectFailure()
    }
}
