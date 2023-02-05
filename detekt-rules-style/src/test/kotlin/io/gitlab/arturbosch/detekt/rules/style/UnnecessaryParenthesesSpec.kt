package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Named
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class UnnecessaryParenthesesSpec {
    @ParameterizedTest
    @MethodSource("cases")
    fun `with unnecessary parentheses on val assignment`(testCase: RuleTestCase) {
        val code = "val local = (5)"

        assertThat(testCase.rule.lint(code)).hasSize(1)
    }

    @ParameterizedTest
    @MethodSource("cases")
    fun `with unnecessary parentheses on val assignment operation`(testCase: RuleTestCase) {
        val code = "val local = (5 + 3)"

        assertThat(testCase.rule.lint(code)).hasSize(1)
    }

    @ParameterizedTest
    @MethodSource("cases")
    fun `with unnecessary parentheses on function call`(testCase: RuleTestCase) {
        val code = "val local = 3.plus((5))"

        assertThat(testCase.rule.lint(code)).hasSize(1)
    }

    @ParameterizedTest
    @MethodSource("cases")
    fun `unnecessary parentheses in other parentheses`(testCase: RuleTestCase) {
        val code = """
            fun x(a: String, b: String) {
                if ((a equals b)) {
                    println("Test")
                }
            }
        """.trimIndent()

        assertThat(testCase.rule.lint(code)).hasSize(1)
    }

    @ParameterizedTest
    @MethodSource("cases")
    fun `does not report unnecessary parentheses around lambdas`(testCase: RuleTestCase) {
        val code = """
            fun function (a: (input: String) -> Unit) {
                a.invoke("TEST")
            }

            fun test() {
                function({ input -> println(input) })
            }
        """.trimIndent()

        assertThat(testCase.rule.lint(code)).isEmpty()
    }

    @ParameterizedTest
    @MethodSource("cases")
    fun `doesn't report function calls containing lambdas and other parameters`(testCase: RuleTestCase) {
        val code = """
            fun function (integer: Int, a: (input: String) -> Unit) {
                a.invoke("TEST")
            }

            fun test() {
                function(1, { input -> println(input) })
            }
        """.trimIndent()

        assertThat(testCase.rule.lint(code)).isEmpty()
    }

    @ParameterizedTest
    @MethodSource("cases")
    fun `does not report unnecessary parentheses when assigning a lambda to a val`(testCase: RuleTestCase) {
        val code = """
            fun f() {
                instance.copy(value = { false })
            }
        """.trimIndent()

        assertThat(testCase.rule.lint(code)).isEmpty()
    }

    @ParameterizedTest
    @MethodSource("cases")
    fun `does not report well behaved parentheses`(testCase: RuleTestCase) {
        val code = """
            fun x(a: String, b: String) {
                if (a equals b) {
                    println("Test")
                }
            }
        """.trimIndent()

        assertThat(testCase.rule.lint(code)).isEmpty()
    }

    @ParameterizedTest
    @MethodSource("cases")
    fun `does not report well behaved parentheses in super constructors`(testCase: RuleTestCase) {
        val code = """
            class TestSpek : SubjectSpek({
                describe("a simple test") {
                    it("should do something") {
                    }
                }
            })
        """.trimIndent()

        assertThat(testCase.rule.lint(code)).isEmpty()
    }

    @ParameterizedTest
    @MethodSource("cases")
    fun `does not report well behaved parentheses in constructors`(testCase: RuleTestCase) {
        val code = """
            class TestSpek({
                describe("a simple test") {
                    it("should do something") {
                    }
                }
            })
        """.trimIndent()

        assertThat(testCase.rule.lint(code)).isEmpty()
    }

    @ParameterizedTest
    @MethodSource("cases")
    fun `should not report lambdas within super constructor calls`(testCase: RuleTestCase) {
        val code = """
            class Clazz(
                private val func: (X, Y) -> Z
            ) {
                constructor() : this({ first, second -> true })
            }
        """.trimIndent()

        assertThat(testCase.rule.lint(code)).isEmpty()
    }

    @ParameterizedTest
    @MethodSource("cases")
    fun `should not report call to function with two lambda parameters with one as block body`(testCase: RuleTestCase) {
        val code = """
            class Clazz {
                fun test(first: (Int) -> Unit, second: (Int) -> Unit) {
                    first(1)
                    second(2)
                }

                fun call() {
                    test({ println(it) }) { println(it) }
                }
            }
        """.trimIndent()

        assertThat(testCase.rule.lint(code)).isEmpty()
    }

    @ParameterizedTest
    @MethodSource("cases")
    fun `should not report call to function with two lambda parameters`(testCase: RuleTestCase) {
        val code = """
            class Clazz {
                fun test(first: (Int) -> Unit, second: (Int) -> Unit) {
                    first(1)
                    second(2)
                }

                fun call() {
                    test({ println(it) }, { println(it) })
                }
            }
        """.trimIndent()

        assertThat(testCase.rule.lint(code)).isEmpty()
    }

    @ParameterizedTest
    @MethodSource("cases")
    fun `should not report call to function with multiple lambdas as parameters but also other parameters`(
        testCase: RuleTestCase,
    ) {
        val code = """
            class Clazz {
                fun test(text: String, first: () -> Unit, second: () -> Unit) {
                    first()
                    second()
                }

                fun call() {
                    test("hello", { println(it) }) { println(it) }
                }
            }
        """.trimIndent()

        assertThat(testCase.rule.lint(code)).isEmpty()
    }

    @ParameterizedTest
    @MethodSource("cases")
    fun `should not report interface delegation with parenthesis - #3851`(testCase: RuleTestCase) {
        val code = """
            class Clazz: Comparable<String> by ("hello".filter { it != 'l' })
        """.trimIndent()

        assertThat(testCase.rule.lint(code)).isEmpty()
    }

    @ParameterizedTest
    @MethodSource("cases")
    fun `numeric expressions when precedence is unclear`(testCase: RuleTestCase) {
        val code = """
            val a1 = (1 * 2) + 3
            val a2 = (1 / 2) + 3
            val a3 = (1 % 2) + 3

            val b1 = 3 + (1 * 2)
            val b2 = 3 + (1 / 2)
            val b3 = 3 + (1 % 2)

            val c = (4 + 5) * 3 // parens required
        """.trimIndent()

        assertThat(testCase.rule.lint(code)).hasSize(if (testCase.allowForUnclearPrecedence) 0 else 6)
    }

    @ParameterizedTest
    @MethodSource("cases")
    fun `numeric expressions when precedence is clear`(testCase: RuleTestCase) {
        val code = """
            val a1 = (1 + 2)
            val a2 = (1 * 2)
            val a3 = (1 + 2 * 3)
            val b1 = (1 + 2) + 3
            val b2 = (1 * 2) * 3
        """.trimIndent()

        assertThat(testCase.rule.lint(code)).hasSize(5)
    }

    @ParameterizedTest
    @MethodSource("cases")
    fun `boolean expressions when precedence is unclear`(testCase: RuleTestCase) {
        val code = """
            val a1 = (true && false) || false
            val a2 = (true && false) || (false && true) // 2 warnings when disallowed
            val b = false || (true && false)
            val c = (true || false) && false // parens required
        """.trimIndent()

        assertThat(testCase.rule.lint(code)).hasSize(if (testCase.allowForUnclearPrecedence) 0 else 4)
    }

    @ParameterizedTest
    @MethodSource("cases")
    fun `boolean expressions when precedence is clear`(testCase: RuleTestCase) {
        val code = """
            val a1 = (true && false)
            val a2 = (true || false)
            val a3 = (true && false || false)
            val b1 = (true && false) && false
            val b2 = (true || false) || false
        """.trimIndent()

        assertThat(testCase.rule.lint(code)).hasSize(5)
    }

    @ParameterizedTest
    @MethodSource("cases")
    fun `infix operators when precedence is unclear`(testCase: RuleTestCase) {
        val code = """
            val d = (true and false) or false
            val e = false or (true and false) // parens required
        """.trimIndent()

        assertThat(testCase.rule.lint(code)).hasSize(if (testCase.allowForUnclearPrecedence) 0 else 1)
    }

    @ParameterizedTest
    @MethodSource("cases")
    fun `elvis operators when precedence is unclear`(testCase: RuleTestCase) {
        val code = """
            val a1 = null ?: (1 to 2) // parens required
            val a2 = (null ?: 1) to 2

            val b1 = null ?: (1 == 2) // parens required
            val b2 = (null ?: 1) == 2

            val c1 = null ?: (1 > 2) // parens required
            val c2 = (null ?: 1) > 2

            val d1 = null ?: (1 in 2) // parens required
            val d2 = (null ?: 1) in 2
        """.trimIndent()

        assertThat(testCase.rule.lint(code)).hasSize(if (testCase.allowForUnclearPrecedence) 0 else 4)
    }

    @ParameterizedTest
    @MethodSource("cases")
    fun `range operator when precedence is unclear`(testCase: RuleTestCase) {
        val code = """
            val a = (1 - 2)..(3 + 4)
            val b = (1 / 2)..(3 * 4)
            val c = (1 ?: 2)..(3 ?: 4) // parens required
            val d = (1 to 2)..(3 to 4) // parens required
        """.trimIndent()

        assertThat(testCase.rule.lint(code)).hasSize(if (testCase.allowForUnclearPrecedence) 0 else 4)
    }

    @ParameterizedTest
    @MethodSource("cases")
    fun `multiple wrapping parentheses`(testCase: RuleTestCase) {
        val code = """
            val a = ((false || (((true && false)))))
        """.trimIndent()

        assertThat(testCase.rule.lint(code)).hasSize(if (testCase.allowForUnclearPrecedence) 4 else 5)
    }

    companion object {
        class RuleTestCase(val allowForUnclearPrecedence: Boolean) {
            val rule = UnnecessaryParentheses(
                TestConfig("allowForUnclearPrecedence" to allowForUnclearPrecedence)
            )
        }

        @JvmStatic
        fun cases(): List<Arguments> {
            return listOf(
                Arguments.of(
                    Named.of("Without allow for unclear precedence", RuleTestCase(allowForUnclearPrecedence = false))
                ),
                Arguments.of(
                    Named.of("With allow for unclear precedence", RuleTestCase(allowForUnclearPrecedence = true))
                ),
            )
        }
    }
}
