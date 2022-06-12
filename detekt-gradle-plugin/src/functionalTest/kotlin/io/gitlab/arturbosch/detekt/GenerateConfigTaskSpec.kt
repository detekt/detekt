package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.testkit.DslTestBuilder
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test

class GenerateConfigTaskSpec {

    @Test
    fun `can be executed without any configuration`() {
        val builder = DslTestBuilder.kotlin()
        val gradleRunner = builder.withConfigFile("config/detekt/detekt.yml").build()

        gradleRunner.runTasksAndCheckResult("detektGenerateConfig") { result ->
            assertThat(result.task(":detektGenerateConfig")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
            assertThat(projectFile("config/detekt/detekt.yml")).exists()
        }
    }

    @Test
    fun `chooses the last config file when configured`() {
        val builder = DslTestBuilder.kotlin()
        val gradleRunner = builder.withDetektConfig(
            """
                    |detekt {
                    |   config = files("config/detekt/detekt.yml", "config/other/detekt.yml")
                    |}
            """
        ).withConfigFile("config/detekt/detekt.yml").build()
        gradleRunner.writeProjectFile("config/other/detekt.yml", content = "")

        gradleRunner.runTasksAndCheckResult("detektGenerateConfig") { result ->
            assertThat(result.task(":detektGenerateConfig")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
            assertThat(projectFile("config/other/detekt.yml")).exists()
        }
    }
}
