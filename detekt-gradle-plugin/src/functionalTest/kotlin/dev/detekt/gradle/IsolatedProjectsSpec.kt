package dev.detekt.gradle

import dev.detekt.gradle.testkit.DslTestBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class IsolatedProjectsSpec {
    @Test
    fun `detekt task supports isolated projects`() {
        val gradleRunner = DslTestBuilder.kotlin().build()

        val buildResult = gradleRunner.runTasks("--isolated-projects", "detekt")

        assertThat(buildResult.output).contains("Isolated Projects is an incubating feature.")
    }

    @Nested
    inner class `Create baseline task` {
        @Test
        fun `supports isolated projects`() {
            val detektConfig = """
                detekt {
                    baseline = file("build/baseline.xml")
                }
            """.trimIndent()
            val gradleRunner = DslTestBuilder.kotlin()
                .withDetektConfig(detektConfig)
                .build()

            val buildResult = gradleRunner.runTasks("--isolated-projects", "detektBaseline")

            assertThat(buildResult.output).contains("Isolated Projects is an incubating feature.")
        }
    }

    @Nested
    inner class `Generate config task` {
        @Test
        fun `supports isolated projects`() {
            val gradleRunner = DslTestBuilder.kotlin().build()

            val buildResult = gradleRunner.runTasks(
                "--isolated-projects",
                "detektGenerateConfig"
            )

            assertThat(buildResult.output).contains("Isolated Projects is an incubating feature.")
        }
    }
}
