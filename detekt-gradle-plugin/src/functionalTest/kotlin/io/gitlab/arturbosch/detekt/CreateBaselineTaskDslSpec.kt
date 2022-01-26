package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.testkit.DslTestBuilder
import io.gitlab.arturbosch.detekt.testkit.ProjectLayout
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class CreateBaselineTaskDslSpec {
    @ParameterizedTest(name = "Using {0}, detektBaseline task can be executed when baseline file is specified")
    @MethodSource("io.gitlab.arturbosch.detekt.testkit.DslTestBuilder#builders")
    fun baselineTaskExecutableWhenBaselineFileSpecified(builder: DslTestBuilder) {
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

    @ParameterizedTest(name = "Using {0}, detektBaseline task can be executed when baseline file is not specified")
    @MethodSource("io.gitlab.arturbosch.detekt.testkit.DslTestBuilder#builders")
    fun baselineTaskExecutableWhenBaselineFileNotSpecified(builder: DslTestBuilder) {
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

    @ParameterizedTest(name = "Using {0}, detektBaseline task can not be executed when baseline file is specified null")
    @MethodSource("io.gitlab.arturbosch.detekt.testkit.DslTestBuilder#builders")
    fun baselineTaskNotExecutableWhenBaselineFileIsNull(builder: DslTestBuilder) {
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

private const val DEFAULT_BASELINE_FILENAME = "detekt-baseline.xml"
