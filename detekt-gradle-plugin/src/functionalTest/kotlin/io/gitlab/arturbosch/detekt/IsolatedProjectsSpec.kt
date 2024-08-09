package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.testkit.DslTestBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class IsolatedProjectsSpec {
    @Test
    fun `detekt task supports isolated projects`() {
        val gradleRunner = DslTestBuilder.kotlin().build()

        val buildResult = gradleRunner.runTasks("-Dorg.gradle.unsafe.isolated-projects=true", "detekt")

        assertThat(buildResult.output).contains("Isolated projects is an incubating feature.")
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

            val buildResult = gradleRunner.runTasks("-Dorg.gradle.unsafe.isolated-projects=true", "detektBaseline")

            assertThat(buildResult.output).contains("Isolated projects is an incubating feature.")
        }
    }

    @Nested
    inner class `Generate config task` {
        @Test
        fun `supports isolated projects`() {
            val gradleRunner = DslTestBuilder.kotlin().build()

            val buildResult = gradleRunner.runTasks(
                "-Dorg.gradle.unsafe.isolated-projects=true",
                "detektGenerateConfig"
            )

            assertThat(buildResult.output).contains("Isolated projects is an incubating feature.")
        }
    }
}
