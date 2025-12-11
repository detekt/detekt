package dev.detekt.metrics

import dev.detekt.test.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CyclomaticComplexitySpec {

    val defaultFunctionComplexity = 1

    @Nested
    inner class `basic function expressions are tested` {

        @Test
        fun `counts for safe navigation`() {
            val code = compileContentForTest(
                """
                    fun test() = null as? String ?: ""
                """.trimIndent()
            )

            val actual = CyclomaticComplexity.calculate(code)

            assertThat(actual).isEqualTo(defaultFunctionComplexity + 1)
        }

        @Test
        fun `counts 'and' and 'or' expressions`() {
            val code = compileContentForTest(
                """
                    fun test() = if (true || true && false) 1 else 0
                """.trimIndent()
            )

            val actual = CyclomaticComplexity.calculate(code)

            assertThat(actual).isEqualTo(defaultFunctionComplexity + 3)
        }

        @Test
        fun `counts while, continue and break`() {
            val code = compileContentForTest(
                """
                    fun test(i: Int) {
                        var j = i
                        while(true) { // 1
                            if (j == 5) { // 1
                                continue // 1
                            } else if (j == 2) { // 1
                                break // 1
                            } else {
                                j += i
                            }
                        }
                        println("finished")
                    }
                """.trimIndent()
            )

            val actual = CyclomaticComplexity.calculate(code)

            assertThat(actual).isEqualTo(defaultFunctionComplexity + 5)
        }
    }

    @Nested
    inner class `counts function calls used for nesting` {

        private val code = compileContentForTest(
            """
                fun test(i: Int) {
                    (1..10).forEach { println(it) }
                }
            """.trimIndent()
        )

        @Test
        fun `counts them by default`() {
            assertThat(
                CyclomaticComplexity.calculate(code)
            ).isEqualTo(defaultFunctionComplexity + 1)
        }

        @Test
        fun `does not count them when ignored`() {
            assertThat(
                CyclomaticComplexity.calculate(code) {
                    ignoreNestingFunctions = true
                }
            ).isEqualTo(defaultFunctionComplexity)
        }

        @Test
        fun `does not count when forEach is not specified`() {
            assertThat(
                CyclomaticComplexity.calculate(code) {
                    nestingFunctions = emptySet()
                }
            ).isEqualTo(defaultFunctionComplexity)
        }

        @Test
        fun `counts them when forEach is specified`() {
            assertThat(
                CyclomaticComplexity.calculate(code) {
                    nestingFunctions = setOf("forEach")
                }
            ).isEqualTo(defaultFunctionComplexity + 1)
        }
    }

    @Nested
    inner class `counts local functions` {
        private val code = compileContentForTest(
            """
                fun test(): String {
                    fun local(flag: boolean) = if (flag) "a" else "b"
                    return local(true)
                }
            """.trimIndent()
        )

        @Test
        fun `counts them by default`() {
            assertThat(
                CyclomaticComplexity.calculate(code)
            ).isEqualTo(defaultFunctionComplexity + 2)
        }

        @Test
        fun `counts them as one when ignored`() {
            assertThat(
                CyclomaticComplexity.calculate(code) {
                    ignoreLocalFunctions = true
                }
            ).isEqualTo(defaultFunctionComplexity + 1)
        }
    }

    @Nested
    inner class `ignoreSimpleWhenEntries is false` {

        @Test
        fun `counts simple when branches as 1`() {
            val function = compileContentForTest(
                """
                    fun test() {
                        when (System.currentTimeMillis()) {
                            0 -> println("Epoch!")
                            1 -> println("1 past epoch.")
                            else -> println("Meh")
                        }
                    }
                """.trimIndent()
            ).getFunctionByName("test")

            val actual = CyclomaticComplexity.calculate(function) {
                ignoreSimpleWhenEntries = false
            }

            assertThat(actual).isEqualTo(defaultFunctionComplexity + 3)
        }

        @Test
        fun `counts block when branches as 1`() {
            val function = compileContentForTest(
                """
                    fun test() {
                        when (System.currentTimeMillis()) {
                            0 -> {
                                println("Epoch!")
                            }
                            1 -> println("1 past epoch.")
                            else -> println("Meh")
                        }
                    }
                """.trimIndent()
            ).getFunctionByName("test")

            val actual = CyclomaticComplexity.calculate(function) {
                ignoreSimpleWhenEntries = false
            }

            assertThat(actual).isEqualTo(defaultFunctionComplexity + 3)
        }
    }

    @Nested
    inner class `ignoreSimpleWhenEntries is true` {

        @Test
        fun `counts a when with only simple branches as 1`() {
            val function = compileContentForTest(
                """
                    fun test() {
                        when (System.currentTimeMillis()) {
                            0 -> println("Epoch!")
                            1 -> println("1 past epoch.")
                            else -> println("Meh")
                        }
                    }
                """.trimIndent()
            ).getFunctionByName("test")

            val actual = CyclomaticComplexity.calculate(function) {
                ignoreSimpleWhenEntries = true
            }

            assertThat(actual).isEqualTo(defaultFunctionComplexity + 1)
        }

        @Test
        fun `does not count simple when branches`() {
            val function = compileContentForTest(
                """
                    fun test() {
                        when (System.currentTimeMillis()) {
                            0 -> {
                                println("Epoch!")
                                println("yay")
                            }
                            1 -> {
                                println("1 past epoch!")
                            }
                            else -> println("Meh")
                        }
                    }
                """.trimIndent()
            ).getFunctionByName("test")

            val actual = CyclomaticComplexity.calculate(function) {
                ignoreSimpleWhenEntries = true
            }

            assertThat(actual).isEqualTo(defaultFunctionComplexity + 2)
        }

        @Test
        fun `counts block when branches as 1`() {
            val function = compileContentForTest(
                """
                    fun test() {
                        when (System.currentTimeMillis()) {
                            0 -> {
                                println("Epoch!")
                                println("yay!")
                            }
                            1 -> {
                                println("1 past epoch.")
                                println("yay?")
                            }
                            2 -> println("shrug")
                            else -> println("Meh")
                        }
                    }
                """.trimIndent()
            ).getFunctionByName("test")

            val actual = CyclomaticComplexity.calculate(function) {
                ignoreSimpleWhenEntries = true
            }

            assertThat(actual).isEqualTo(defaultFunctionComplexity + 2)
        }
    }
}

private fun KtElement.getFunctionByName(name: String): KtNamedFunction {
    val node = getChildOfType<KtNamedFunction>() ?: error("Expected node of type ${KtNamedFunction::class}")
    val identifier = node.nameAsName?.identifier

    require(identifier == name) {
        "Node should be $name, but was $identifier"
    }

    return node
}
