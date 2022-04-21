package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.testkit.DslTestBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class ConfigurationCacheSpec {
    @ParameterizedTest(name = "Given {0}, can be loaded from the configuration cache")
    @CsvSource(
        "regular invocation, 'detekt'",
        "dry-run invocation, 'detekt,-Pdetekt-dry-run=true'",
    )
    @Suppress("UnusedPrivateMember") // `unused` is used in the parameterized test name
    fun detektConfigCache(unused: String, arguments: String) {
        val gradleRunner = DslTestBuilder.kotlin().build()

        // First run primes the cache
        gradleRunner.runTasks("--configuration-cache", *arguments.split(',').toTypedArray())

        // Second run reuses the cache
        val result = gradleRunner.runTasks("--configuration-cache", *arguments.split(',').toTypedArray())

        assertThat(result.output).contains("Reusing configuration cache.")
    }

    @Nested
    inner class `Create baseline task` {
        @Test
        fun `can be loaded from the configuration cache`() {
            val detektConfig = """
                        |detekt {
                        |   baseline = file("build/baseline.xml")
                        |}
            """
            val gradleRunner = DslTestBuilder.kotlin()
                .dryRun()
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
                .dryRun()
                .build()

            // First run primes the cache
            gradleRunner.runTasks("--configuration-cache", "detektGenerateConfig")

            // Second run reuses the cache
            val result = gradleRunner.runTasks("--configuration-cache", "detektGenerateConfig")

            assertThat(result.output).contains("Reusing configuration cache.")
        }
    }
}
