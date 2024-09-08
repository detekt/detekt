package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private val defaultAllowedComplexity = "allowedComplexity" to "1"

class CyclomaticComplexMethodSpec {

    @Nested
    inner class `different complex constructs` {

        @Test
        fun `counts different loops`() {
            val findings = CyclomaticComplexMethod(TestConfig(defaultAllowedComplexity)).compileAndLint(
                """
                    fun test() {
                        for (i in 1..10) {}
                        while (true) {}
                        do {} while(true)
                        (1..10).forEach {}
                    }
                """.trimIndent()
            )

            assertThat(findings).hasSize(1)
        }

        @Test
        fun `counts catch blocks`() {
            val findings = CyclomaticComplexMethod(TestConfig(defaultAllowedComplexity)).compileAndLint(
                """
                    fun test() {
                        try {} catch(e: IllegalArgumentException) {} catch(e: Exception) {} finally {}
                    }
                """.trimIndent()
            )

            assertThat(findings).hasSize(1)
        }

        @Test
        fun `counts nested conditional statements`() {
            val findings = CyclomaticComplexMethod(TestConfig(defaultAllowedComplexity)).compileAndLint(
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

            assertThat(findings).hasSize(1)
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
            val config = TestConfig(defaultAllowedComplexity, "ignoreNestingFunctions" to "false")
            assertExpectedComplexityValue(code, config, expectedValue = 3)
        }

        @Test
        fun `can ignore nesting functions like 'forEach'`() {
            val config = TestConfig(defaultAllowedComplexity, "ignoreNestingFunctions" to "true")
            assertExpectedComplexityValue(code, config, expectedValue = 2)
        }

        @Test
        fun `skips all if if the nested functions is empty`() {
            val config = TestConfig(defaultAllowedComplexity, "nestingFunctions" to emptyList<String>())
            assertExpectedComplexityValue(code, config, expectedValue = 2)
        }

        @Test
        fun `skips 'forEach' as it is not specified`() {
            val config = TestConfig(defaultAllowedComplexity, "nestingFunctions" to listOf("let", "apply", "also"))
            assertExpectedComplexityValue(code, config, expectedValue = 2)
        }
    }

    @Nested
    inner class `several complex methods` {

        val code = """
            // reports 1 - only if ignoreSingleWhenExpression = false
            fun complexMethodWithSingleWhen1(i: Int) =
                when (i) {
                    1 -> print("one")
                    2 -> print("two")
                    3 -> print("three")
                    else -> print(i)
                }
            
            // reports 1 - only if ignoreSingleWhenExpression = false
            fun complexMethodWithSingleWhen2(i: Int) {
                when (i) {
                    1 -> print("one")
                    2 -> print("two")
                    3 -> print("three")
                    else -> print(i)
                }
            }
            
            // reports 1 - only if ignoreSingleWhenExpression = false
            fun complexMethodWithSingleWhen3(i: Int): String {
                return when (i) {
                    1 -> "one"
                    2 -> "two"
                    3 -> "three"
                    else -> ""
                }
            }
            
            // reports 1 - only if ignoreSingleWhenExpression = false
            fun complexMethodWithSingleWhen4(i: Int) = when (i) {
                1 -> "one"
                2 -> "two"
                3 -> "three"
                else -> ""
            }
            
            // reports 1
            fun complexMethodWith2Statements(i: Int) {
                when (i) {
                    1 -> print("one")
                    2 -> print("two")
                    3 -> print("three")
                    else -> print(i)
                }
                if (i == 1) {
                }
            }
        """.trimIndent()

        @Test
        fun `does not report complex methods with a single when expression`() {
            val config = TestConfig(
                "allowedComplexity" to "4",
                "ignoreSingleWhenExpression" to "true",
            )
            val subject = CyclomaticComplexMethod(config)

            assertThat(subject.compileAndLint(code)).hasStartSourceLocations(SourceLocation(39, 5))
        }

        @Test
        fun `reports all complex methods`() {
            val config = TestConfig("allowedComplexity" to "4")
            val subject = CyclomaticComplexMethod(config)

            val findings = subject.compileAndLint(code)

            assertThat(findings).hasSize(5)
            assertThat(findings).hasStartSourceLocations(
                SourceLocation(2, 5),
                SourceLocation(11, 5),
                SourceLocation(21, 5),
                SourceLocation(31, 5),
                SourceLocation(39, 5)
            )
        }

        @Test
        fun `does not report function that has exactly the allowed complexity`() {
            val config = TestConfig("allowedComplexity" to "6")
            val subject = CyclomaticComplexMethod(config)

            val code = """
                fun complexMethodWith2Statements(i: Int) {
                    when (i) {
                        1 -> print("one")
                        2 -> print("two")
                        3 -> print("three")
                        else -> print(i)
                    }
                    if (i == 1) {
                    }
                }
            """.trimIndent()

            val findings = subject.compileAndLint(code)

            assertThat(findings).isEmpty()
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
            assertThat(CyclomaticComplexMethod(Config.empty).compileAndLint(code)).isEmpty()
        }
    }
}

private fun assertExpectedComplexityValue(code: String, config: TestConfig, expectedValue: Int) {
    val findings = CyclomaticComplexMethod(config).compileAndLint(code)

    assertThat(findings).hasStartSourceLocations(SourceLocation(1, 5))

    assertThat(findings[0].message).contains("(complexity: $expectedValue)")
}
