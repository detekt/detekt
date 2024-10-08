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
        val storeCacheResult = gradleRunner.runTasks("--configuration-cache", "detekt")

        assertThat(storeCacheResult.output).contains("Configuration cache entry stored.")

        // Second run reuses the cache
        val reuseCacheResult = gradleRunner.runTasks("--configuration-cache", "detekt")

        assertThat(reuseCacheResult.output).contains("Reusing configuration cache.")
    }

    @Nested
    inner class `Create baseline task` {
        @Test
        fun `can be loaded from the configuration cache`() {
            val detektConfig = """
                detekt {
                    baseline = file("build/baseline.xml")
                }
            """.trimIndent()
            val gradleRunner = DslTestBuilder.kotlin()
                .withDetektConfig(detektConfig)
                .build()

            // First run primes the cache
            val storeCacheResult = gradleRunner.runTasks("--configuration-cache", "detektBaselineMainSourceSet")

            assertThat(storeCacheResult.output).contains("Configuration cache entry stored.")

            // Second run reuses the cache
            val reuseCacheResult = gradleRunner.runTasks("--configuration-cache", "detektBaselineMainSourceSet")

            assertThat(reuseCacheResult.output).contains("Reusing configuration cache.")
        }
    }

    @Nested
    inner class `Generate config task` {
        @Test
        fun `can be loaded from the configuration cache`() {
            val gradleRunner = DslTestBuilder.kotlin()
                .build()

            // First run generates file
            val generateConfigResult = gradleRunner.runTasks("--configuration-cache", "detektGenerateConfig")

            assertThat(generateConfigResult.output).contains("Configuration cache entry stored.")

            // Second run primes the cache based on the generated file
            val storeCacheResult = gradleRunner.runTasks("--configuration-cache", "detektGenerateConfig")

            assertThat(storeCacheResult.output).contains("Configuration cache entry stored.")

            // Third run reuses the cache
            val reuseCacheResult = gradleRunner.runTasks("--configuration-cache", "detektGenerateConfig")

            assertThat(reuseCacheResult.output).contains("Reusing configuration cache.")
        }
    }
}
