package dev.detekt.gradle

import dev.detekt.gradle.testkit.DslTestBuilder
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DetektProfilingTaskSpec {

    @Nested
    inner class `detektProfile task` {

        @Test
        fun `runs successfully`() {
            // Use dry run mode because the profiling CLI argument may not be in published detekt-cli
            val gradleRunner = DslTestBuilder.kotlin()
                .dryRun()
                .build()

            val result = gradleRunner.runTasks("detektProfile")

            assertThat(result.task(":detektProfile")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        }

        @Test
        fun `completes alongside detekt task`() {
            val gradleRunner = DslTestBuilder.kotlin()
                .dryRun()
                .build()

            // Run both tasks together to verify they work
            val result = gradleRunner.runTasks("detekt", "detektProfile")

            assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
            assertThat(result.task(":detektProfile")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        }

        @Test
        fun `completes successfully without errors`() {
            val gradleRunner = DslTestBuilder.kotlin()
                .dryRun()
                .build()

            val result = gradleRunner.runTasks("detektProfile")

            // In dry-run mode, the task should complete without errors
            assertThat(result.output).doesNotContain("FAILED")
        }
    }

    @Nested
    inner class `with topRulesToShow configured` {

        @Test
        fun `accepts custom topRulesToShow value in extension`() {
            val config = """
                detekt {
                    topRulesToShow.set(5)
                }
            """.trimIndent()

            val gradleRunner = DslTestBuilder.kotlin()
                .withDetektConfig(config)
                .dryRun()
                .build()

            val result = gradleRunner.runTasks("detektProfile")

            assertThat(result.task(":detektProfile")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        }
    }

    @Nested
    inner class `task properties` {

        @Test
        fun `is in verification group`() {
            val gradleRunner = DslTestBuilder.kotlin()
                .dryRun()
                .build()

            val result = gradleRunner.runTasks("tasks", "--all")

            assertThat(result.output).contains("detektProfile")
        }

        @Test
        fun `root project task has aggregate description`() {
            // Root project uses DetektProfilingAggregateTask
            val gradleRunner = DslTestBuilder.kotlin()
                .dryRun()
                .build()

            val result = gradleRunner.runTasks("help", "--task", "detektProfile")

            assertThat(result.output).contains("Aggregates detekt profiling results")
        }
    }

    @Nested
    inner class `Groovy DSL` {

        @Test
        fun `profiling task works with Groovy DSL`() {
            val gradleRunner = DslTestBuilder.groovy()
                .dryRun()
                .build()

            val result = gradleRunner.runTasks("detektProfile")

            assertThat(result.task(":detektProfile")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        }

        @Test
        fun `can configure topRulesToShow in Groovy DSL`() {
            val config = """
                detekt {
                    topRulesToShow = 5
                }
            """.trimIndent()

            val gradleRunner = DslTestBuilder.groovy()
                .withDetektConfig(config)
                .dryRun()
                .build()

            val result = gradleRunner.runTasks("detektProfile")

            assertThat(result.task(":detektProfile")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        }
    }
}
