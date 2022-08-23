package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.testkit.DslTestBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ConfigurationCacheSpec {
    @Test
    fun `detekt task can be loaded from the configuration cache`() {
        val gradleRunner = DslTestBuilder.kotlin().build()

        // First run primes the cache
        gradleRunner.runTasks("--configuration-cache", "detekt")

        // Second run reuses the cache
        val result = gradleRunner.runTasks("--configuration-cache", "detekt")

        assertThat(result.output).contains("Reusing configuration cache.")
    }

    @Nested
    inner class `Create baseline task` {
        @Test
        fun `can be loaded from the configuration cache`() {
            @Suppress("TrimMultilineRawString")
            val detektConfig = """
                |detekt {
                |   baseline = file("build/baseline.xml")
                |}
            """
            val gradleRunner = DslTestBuilder.kotlin()
                .withDetektConfig(detektConfig)
                .build()

            // First run primes the cache
            gradleRunner.runTasks("--configuration-cache", "detektBaseline")

            // Second run reuses the cache
            val result = gradleRunner.runTasks("--configuration-cache", "detektBaseline")

            assertThat(result.output).contains("Reusing configuration cache.")
        }
    }

    @Nested
    inner class `Generate config task` {
        @Test
        fun `can be loaded from the configuration cache`() {
            val gradleRunner = DslTestBuilder.kotlin()
                .build()

            // First run primes the cache
            gradleRunner.runTasks("--configuration-cache", "detektGenerateConfig")

            // Second run reuses the cache
            val result = gradleRunner.runTasks("--configuration-cache", "detektGenerateConfig")

            assertThat(result.output).contains("Reusing configuration cache.")
        }
    }
}
