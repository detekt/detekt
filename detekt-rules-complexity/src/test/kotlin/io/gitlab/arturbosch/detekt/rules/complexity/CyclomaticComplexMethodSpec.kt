package io.gitlab.arturbosch.detekt.rules.complexity

import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.isThresholded
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private val defaultThreshold = "threshold" to "1"

class CyclomaticComplexMethodSpec {

    val defaultComplexity = 1

    @Nested
    inner class `different complex constructs` {

        @Test
        fun `counts different loops`() {
            val findings = CyclomaticComplexMethod(TestConfig(defaultThreshold)).compileAndLint(
                """
                    fun test() {
                        for (i in 1..10) {}
                        while (true) {}
                        do {} while(true)
                        (1..10).forEach {}
                    }
                """.trimIndent()
            )

            assertThat(findings.first()).isThresholded().withValue(defaultComplexity + 4)
        }

        @Test
        fun `counts catch blocks`() {
            val findings = CyclomaticComplexMethod(TestConfig(defaultThreshold)).compileAndLint(
                """
                    fun test() {
                        try {} catch(e: IllegalArgumentException) {} catch(e: Exception) {} finally {}
                    }
                """.trimIndent()
            )

            assertThat(findings.first()).isThresholded().withValue(defaultComplexity + 2)
        }

        @Test
        fun `counts nested conditional statements`() {
            val findings = CyclomaticComplexMethod(TestConfig(defaultThreshold)).compileAndLint(
                """
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
                """.trimIndent()
            )

            assertThat(findings.first()).isThresholded().withValue(defaultComplexity + 4)
        }
    }

    @Nested
    inner class `nesting functions` {

        val code = """
            fun test() {
                for (i in 1..10) {}
                (1..10).forEach {}
            }
        """.trimIndent()

        @Test
        fun `counts three with nesting function 'forEach'`() {
            val config = TestConfig(defaultThreshold, "ignoreNestingFunctions" to "false")
            assertExpectedComplexityValue(code, config, expectedValue = 3)
        }

        @Test
        fun `can ignore nesting functions like 'forEach'`() {
            val config = TestConfig(defaultThreshold, "ignoreNestingFunctions" to "true")
            assertExpectedComplexityValue(code, config, expectedValue = 2)
        }

        @Test
        fun `skips all if if the nested functions is empty`() {
            val config = TestConfig(defaultThreshold, "nestingFunctions" to "")
            assertExpectedComplexityValue(code, config, expectedValue = 2)
        }

        @Test
        fun `skips 'forEach' as it is not specified`() {
            val config = TestConfig(defaultThreshold, "nestingFunctions" to "let,apply,also")
            assertExpectedComplexityValue(code, config, expectedValue = 2)
        }

        @Test
        fun `skips 'forEach' as it is not specified list`() {
            val config = TestConfig(defaultThreshold, "nestingFunctions" to listOf("let", "apply", "also"))
            assertExpectedComplexityValue(code, config, expectedValue = 2)
        }
    }

    @Nested
    inner class `several complex methods` {

        val path = resourceAsPath("ComplexMethods.kt")

        @Test
        fun `does not report complex methods with a single when expression`() {
            val config = TestConfig(
                "threshold" to "4",
                "ignoreSingleWhenExpression" to "true",
            )
            val subject = CyclomaticComplexMethod(config)

            assertThat(subject.lint(path)).hasStartSourceLocations(SourceLocation(43, 5))
        }

        @Test
        fun `reports all complex methods`() {
            val config = TestConfig("threshold" to "4")
            val subject = CyclomaticComplexMethod(config)

            assertThat(subject.lint(path)).hasStartSourceLocations(
                SourceLocation(6, 5),
                SourceLocation(15, 5),
                SourceLocation(25, 5),
                SourceLocation(35, 5),
                SourceLocation(43, 5)
            )
        }

        @Test
        fun `does not trip for a reasonable amount of simple when entries when ignoreSimpleWhenEntries is true`() {
            val config = TestConfig("ignoreSimpleWhenEntries" to "true")
            val subject = CyclomaticComplexMethod(config)
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
            """.trimIndent()

            val findings = subject.compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `function containing object literal with many overridden functions` {

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
        """.trimIndent()

        @Test
        fun `should not count these overridden functions to base functions complexity`() {
            assertThat(CyclomaticComplexMethod().compileAndLint(code)).isEmpty()
        }
    }
}

private fun assertExpectedComplexityValue(code: String, config: TestConfig, expectedValue: Int) {
    val findings = CyclomaticComplexMethod(config).lint(code)

    assertThat(findings).hasStartSourceLocations(SourceLocation(1, 5))

    assertThat(findings.first())
        .isThresholded()
        .withValue(expectedValue)
        .withThreshold(1)
}
