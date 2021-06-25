package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.testkit.DslTestBuilder
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object ConfigurationCacheSpec : Spek({
    describe("Detekt task") {
        listOf(
            "regular invocation" to arrayOf("detekt"),
            "dry-run invocation" to arrayOf("detekt", "-Pdetekt-dry-run=true"),
        ).forEach { (context, arguments) ->
            context("given $context") {
                it("can be loaded from the configuration cache") {
                    val gradleRunner = DslTestBuilder.kotlin().build()

                    // First run primes the cache
                    gradleRunner.runTasks("--configuration-cache", *arguments)

                    // Second run reuses the cache
                    val result = gradleRunner.runTasks("--configuration-cache", *arguments)

                    assertThat(result.output).contains("Reusing configuration cache.")
                }
            }
        }
    }

    describe("Create baseline task") {
        it("can be loaded from the configuration cache") {
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

    describe("Generate config task") {
        it("can be loaded from the configuration cache") {
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
})
