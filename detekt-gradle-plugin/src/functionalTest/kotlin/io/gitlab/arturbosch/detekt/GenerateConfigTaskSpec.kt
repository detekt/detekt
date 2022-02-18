package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.testkit.DslTestBuilder
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class GenerateConfigTaskSpec {

    @ParameterizedTest(name = "Using {0}, can be executed without any configuration")
    @MethodSource("io.gitlab.arturbosch.detekt.testkit.DslTestBuilder#builders")
    fun emptyConfig(builder: DslTestBuilder) {
        val gradleRunner = builder.build()

        gradleRunner.runTasksAndCheckResult("detektGenerateConfig") { result ->
            assertThat(result.task(":detektGenerateConfig")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
            assertThat(projectFile("config/detekt/detekt.yml")).exists()
        }
    }

    @ParameterizedTest(name = "Using {0}, chooses the last config file when configured")
    @MethodSource("io.gitlab.arturbosch.detekt.testkit.DslTestBuilder#builders")
    fun `chooses the last config file when configured`(builder: DslTestBuilder) {
        val gradleRunner = builder.withDetektConfig(
            """
                    |detekt {
                    |   config = files("config/detekt/detekt.yml", "config/other/detekt.yml")
                    |}
                """
        ).build()

        gradleRunner.runTasksAndCheckResult("detektGenerateConfig") { result ->
            assertThat(result.task(":detektGenerateConfig")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
            assertThat(projectFile("config/other/detekt.yml")).exists()
        }
    }
}
