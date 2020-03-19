package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.isThresholded
import io.gitlab.arturbosch.detekt.test.lint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ComplexMethodSpec : Spek({

    val defaultComplexity = 1

    describe("ComplexMethod rule") {

        context("different complex constructs") {

            it("counts different loops") {
                val findings = ComplexMethod(threshold = 1).compileAndLint("""
                    fun test() {
                        for (i in 1..10) {}
                        while (true) {}
                        do {} while(true)
                        (1..10).forEach {}
                    }
                """.trimIndent())

                assertThat(findings.first()).isThresholded().withValue(defaultComplexity + 4)
            }

            it("counts catch blocks") {
                val findings = ComplexMethod(threshold = 1).compileAndLint("""
                    fun test() {
                        try {} catch(e: IllegalArgumentException) {} catch(e: Exception) {} finally {}
                    }
                """.trimIndent())

                assertThat(findings.first()).isThresholded().withValue(defaultComplexity + 2)
            }

            it("counts nested conditional statements") {
                val findings = ComplexMethod(threshold = 1).compileAndLint("""
                    fun test() {
                        try {
                            while (true) {
                                if (true) {
                                    when ("string") {
                                        "" -> println()
                                        else -> println()
                                    }
                                }
                            }
                        } finally {
                            // only catches count
                        }
                    }
                """.trimIndent())

                assertThat(findings.first()).isThresholded().withValue(defaultComplexity + 4)
            }
        }

        context("nesting functions") {

            val code = """
                    fun test() {
                        for (i in 1..10) {}
                        (1..10).forEach {}
                    }
                """.trimIndent()

            fun execute(config: TestConfig, expectedValue: Int) {
                val findings = ComplexMethod(config, threshold = 1).lint(code)

                assertThat(findings).hasSourceLocations(SourceLocation(1, 5))

                assertThat(findings.first())
                    .isThresholded()
                    .withValue(expectedValue)
                    .withThreshold(1)
            }

            it("counts three with nesting function 'forEach'") {
                val config = TestConfig(mapOf(ComplexMethod.IGNORE_NESTING_FUNCTIONS to "false"))
                execute(config, expectedValue = 3)
            }

            it("can ignore nesting functions like 'forEach'") {
                val config = TestConfig(mapOf(ComplexMethod.IGNORE_NESTING_FUNCTIONS to "true"))
                execute(config, expectedValue = 2)
            }

            it("defaults to a predefined set of nested functions for compatibility when empty") {
                val config = TestConfig(mapOf(ComplexMethod.NESTING_FUNCTIONS to ""))
                execute(config, expectedValue = 3)
            }

            it("skips 'forEach' as it is not specified") {
                val config = TestConfig(mapOf(ComplexMethod.NESTING_FUNCTIONS to "let,apply,also"))
                execute(config, expectedValue = 2)
            }
        }

        context("several complex methods") {

            val path = Case.ComplexMethods.path()

            it("does not report complex methods with a single when expression") {
                val config = TestConfig(mapOf(
                    ComplexMethod.IGNORE_SINGLE_WHEN_EXPRESSION to "true"))
                val subject = ComplexMethod(config, threshold = 4)

                assertThat(subject.lint(path)).hasSourceLocations(SourceLocation(43, 5))
            }

            it("reports all complex methods") {
                val subject = ComplexMethod(threshold = 4)

                assertThat(subject.lint(path)).hasSourceLocations(
                    SourceLocation(6, 5),
                    SourceLocation(15, 5),
                    SourceLocation(25, 5),
                    SourceLocation(35, 5),
                    SourceLocation(43, 5)
                )
            }

            it("does not trip for a reasonable amount of simple when entries when ignoreSimpleWhenEntries is true") {
                val config = TestConfig(mapOf(ComplexMethod.IGNORE_SIMPLE_WHEN_ENTRIES to "true"))
                val subject = ComplexMethod(config)
                val code = """
                     fun f() {
                        val map = HashMap<Any, String>()
                        for ((key, value) in map) {
                            when (key) {
                                is Int -> print("int")
                                is String -> print("String")
                                is Float -> print("Float")
                                is Double -> print("Double")
                                is Byte -> print("Byte")
                                is Short -> print("Short")
                                is Long -> print("Long")
                                is Boolean -> print("Boolean")
                                else -> throw IllegalArgumentException("Unexpected type value")
                            }
                        }
                    }
                """

                val findings = subject.compileAndLint(code)
                assertThat(findings).isEmpty()
            }
        }

        context("function containing object literal with many overridden functions") {

            val code = """
            fun f(): List<Any> {
                return object : List<Any> {
                    override val size: Int get() = TODO("not implemented")

                    override fun contains(element: Any): Boolean {
                        TODO("not implemented")
                    }

                    override fun containsAll(elements: Collection<Any>): Boolean {
                        TODO("not implemented")
                    }

                    override fun get(index: Int): Any {
                        TODO("not implemented")
                    }

                    override fun indexOf(element: Any): Int {
                        TODO("not implemented")
                    }

                    override fun isEmpty(): Boolean {
                        TODO("not implemented")
                    }

                    override fun iterator(): Iterator<Any> {
                        TODO("not implemented")
                    }

                    override fun lastIndexOf(element: Any): Int {
                        TODO("not implemented")
                    }

                    override fun listIterator(): ListIterator<Any> {
                        TODO("not implemented")
                    }

                    override fun listIterator(index: Int): ListIterator<Any> {
                        TODO("not implemented")
                    }

                    override fun subList(fromIndex: Int, toIndex: Int): List<Any> {
                        TODO("not implemented")
                    }
                }
            }
            """

            it("should not count these overridden functions to base functions complexity") {
                assertThat(ComplexMethod().compileAndLint(code)).isEmpty()
            }
        }
    }
})
