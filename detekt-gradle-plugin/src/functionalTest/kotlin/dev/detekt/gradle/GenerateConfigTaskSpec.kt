package dev.detekt.gradle

import dev.detekt.gradle.testkit.DslTestBuilder
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
    fun `can be executed when the worker api is enabled`() {
        val builder = DslTestBuilder.kotlin()
        // The runner hardcodes --build-cache and the task's cache key (classpath only; the
        // arguments are @Internal) is shared by every generate-config test project, so the
        // task would be satisfied FROM_CACHE without its Worker API branch ever running.
        // --no-build-cache overrides it, and a non-default output path skips the task's
        // "file already exists" short-circuit.
        val gradleRunner = builder.withDetektConfig(
            """
                tasks.detektGenerateConfig {
                   configFile.set(file("config/workerapi/detekt.yml"))
                }
            """.trimIndent()
        ).build()

        gradleRunner.runTasksAndCheckResult(
            "detektGenerateConfig",
            "-Pdetekt.use.worker.api=true",
            "--no-build-cache",
        ) { result ->
            assertThat(result.task(":detektGenerateConfig")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
            assertThat(projectFile("config/workerapi/detekt.yml")).exists()
        }
    }

    @Test
    fun `chooses the last config file when configured`() {
        val builder = DslTestBuilder.kotlin()
        val gradleRunner = builder.withDetektConfig(
            """
                detekt {
                   config.setFrom("config/detekt/detekt.yml", "config/other/detekt.yml")
                }
            """.trimIndent()
        ).withConfigFile("config/detekt/detekt.yml").build()
        gradleRunner.writeProjectFile("config/other/detekt.yml", content = "")

        gradleRunner.runTasksAndCheckResult("detektGenerateConfig") { result ->
            assertThat(result.task(":detektGenerateConfig")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
            assertThat(projectFile("config/other/detekt.yml")).exists()
        }
    }

    @Test
    fun `setting configFile property overrides extension config`() {
        val builder = DslTestBuilder.kotlin()
        val gradleRunner = builder.withDetektConfig(
            """
                detekt {
                   config.setFrom("config/wrongpath1/detekt.yml", "config/wrongpath2/detekt.yml")
                }
                
                tasks.detektGenerateConfig {
                   configFile.set(file("config/correctpath/detekt.yml"))
                }
            """.trimIndent()
        ).withConfigFile("config/detekt/detekt.yml").build()
        gradleRunner.writeProjectFile("config/other/detekt.yml", content = "")

        gradleRunner.runTasksAndCheckResult("detektGenerateConfig") { result ->
            assertThat(result.task(":detektGenerateConfig")?.outcome).isIn(TaskOutcome.SUCCESS, TaskOutcome.FROM_CACHE)
            assertThat(projectFile("config/correctpath/detekt.yml")).exists()
        }
    }
}
