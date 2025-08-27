package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Finding
import dev.detekt.api.TextLocation
import dev.detekt.test.TestConfig
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lint
import io.gitlab.arturbosch.detekt.rules.style.BracesOnIfStatements.BracePolicy
import io.gitlab.arturbosch.detekt.rules.style.BracesOnIfStatementsSpec.Companion.NOT_RELEVANT
import io.gitlab.arturbosch.detekt.rules.style.BracesOnIfStatementsSpec.Companion.options
import io.gitlab.arturbosch.detekt.rules.style.BracesOnIfStatementsSpec.Companion.test
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.ThrowingConsumer
import org.junit.jupiter.api.DynamicContainer.dynamicContainer
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

/**
 * Note: this class makes extensive use of dynamic tests and containers, few tips for maintenance:
 *  * Numbers are made relative to the code snippet rather than the whole code passed to Kotlin compiler. [test].
 *  * To debug a specific test case, remove all other test cases from the same method.
 *  * Test coverage is added for all possible configuration options, see [NOT_RELEVANT].
 */
@Suppress(
    "ClassName",
    "LongMethod",
    "CommentOverPrivateProperty"
)
class BracesOnIfStatementsSpec {

    @Test
    fun `validate behavior of occurrence function`() {
        val code = "fun f() { if (true) else if (true) if (true) true }"
        assertThat("if"(1)(code)).isEqualTo(10 to 12)
        assertThat("if"(2)(code)).isEqualTo(25 to 27)
        assertThat("if"(3)(code)).isEqualTo(35 to 37)
        assertThat("else"(1)(code)).isEqualTo(20 to 24)
        assertThatCode { "if"(4)(code) }.isInstanceOf(IllegalArgumentException::class.java)
        assertThatCode { "if"(0)(code) }.isInstanceOf(IllegalArgumentException::class.java)
        assertThatCode { "else"(2)(code) }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Nested
    inner class singleLine {

        @Nested
        inner class `=always` {

            private fun flag(code: String, vararg locations: (String) -> Pair<Int, Int>): DynamicNode {
                val validTopLevelCode = """
                    fun main() {
                        $code
                    }
                """.trimIndent()
                return testCombinations(BracePolicy.Always.config, NOT_RELEVANT, validTopLevelCode, *locations)
            }

            @TestFactory
            fun `missing braces are flagged`() = listOf(
                flag("if (true) println()", "if"(1)),
                flag("if (true) println() else println()", "if"(1), "else"(1)),
                flag(
                    "if (true) println() else if (true) println()",
                    "if"(1),
                    "if"(2)
                ),
                flag(
                    "if (true) println() else if (true) println() else println()",
                    "if"(1),
                    "if"(2),
                    "else"(2)
                ),
                flag(
                    "if (true) println() else if (true) println() else if (true) println()",
                    "if"(1),
                    "if"(2),
                    "if"(3)
                ),
                flag(
                    "if (true) println() else if (true) println() else if (true) println() else println()",
                    "if"(1),
                    "if"(2),
                    "if"(3),
                    "else"(3)
                ),
            )

            @TestFactory
            fun `existing braces are accepted`() = listOf(
                flag("if (true) { println() }", *NOTHING),
                flag("if (true) { println() } else { println() }", *NOTHING),
                flag("if (true) { println() } else if (true) { println() }", *NOTHING),
                flag("if (true) { println() } else if (true) { println() } else { println() }", *NOTHING),
                flag("if (true) { println() } else if (true) { println() } else if (true) { println() }", *NOTHING),
                flag(
                    "if (true) { println() } else if (true) { println() } else if (true) { println() } else { println() }",
                    *NOTHING
                ),
            )

            @TestFactory
            fun `partially missing braces are flagged (first branch)`() = listOf(
                flag("if (true) { println() } else println()", "else"(1)),
                flag("if (true) { println() } else if (true) println()", "if"(2)),
                flag(
                    "if (true) { println() } else if (true) println() else println()",
                    "if"(2),
                    "else"(2)
                ),
                flag(
                    "if (true) { println() } else if (true) println() else if (true) println()",
                    "if"(2),
                    "if"(3)
                ),
                flag(
                    "if (true) { println() } else if (true) println() else if (true) println() else println()",
                    "if"(2),
                    "if"(3),
                    "else"(3)
                ),
            )

            @TestFactory
            fun `partially missing braces are flagged (last branch)`() = listOf(
                flag("if (true) println() else { println() }", "if"(1)),
                flag("if (true) println() else if (true) { println() }", "if"(1)),
                flag(
                    "if (true) println() else if (true) println() else { println() }",
                    "if"(1),
                    "if"(2)
                ),
                flag(
                    "if (true) println() else if (true) println() else if (true) { println() }",
                    "if"(1),
                    "if"(2)
                ),
                flag(
                    "if (true) println() else if (true) println() else if (true) println() else { println() }",
                    "if"(1),
                    "if"(2),
                    "if"(3)
                ),
            )

            @TestFactory
            fun `partially missing braces are flagged (middle branches)`() = listOf(
                flag(
                    "if (true) println() else if (true) { println() } else println()",
                    "if"(1),
                    "else"(2)
                ),
                flag(
                    "if (true) println() else if (true) { println() } else if (true) println()",
                    "if"(1),
                    "if"(3)
                ),
                flag(
                    "if (true) println() else if (true) { println() } else if (true) { println() } else println()",
                    "if"(1),
                    "else"(3)
                ),
            )
        }

        @Nested
        inner class `=never` {

            private fun flag(code: String, vararg locations: (String) -> Pair<Int, Int>): DynamicNode {
                val validTopLevelCode = """
                    fun main() {
                        $code
                    }
                """.trimIndent()
                return testCombinations(BracePolicy.Never.config, NOT_RELEVANT, validTopLevelCode, *locations)
            }

            @TestFactory fun `no braces are accepted`() = listOf(
                flag("if (true) println()", *NOTHING),
                flag("if (true) println() else println()", *NOTHING),
                flag("if (true) println() else if (true) println()", *NOTHING),
                flag("if (true) println() else if (true) println() else println()", *NOTHING),
                flag("if (true) println() else if (true) println() else if (true) println()", *NOTHING),
                flag("if (true) println() else if (true) println() else if (true) println() else println()", *NOTHING),
            )

            @TestFactory
            fun `existing braces are flagged`() = listOf(
                flag("if (true) { println() }", "if"(1)),
                flag("if (true) { println() } else { println() }", "if"(1), "else"(1)),
                flag("if (true) { println() } else if (true) { println() }", "if"(1), "if"(2)),
                flag(
                    "if (true) { println() } else if (true) { println() } else { println() }",
                    "if"(1),
                    "if"(2),
                    "else"(2)
                ),
                flag(
                    "if (true) { println() } else if (true) { println() } else if (true) { println() }",
                    "if"(1),
                    "if"(2),
                    "if"(3)
                ),
                flag(
                    "if (true) { println() } else if (true) { println() } else if (true) { println() } else { println() }",
                    "if"(1),
                    "if"(2),
                    "if"(3),
                    "else"(3)
                ),
            )

            @TestFactory
            fun `partially extra braces are flagged (first branch)`() = listOf(
                flag("if (true) { println() }", "if"(1)),
                flag("if (true) { println() } else println()", "if"(1)),
                flag("if (true) { println() } else if (true) println()", "if"(1)),
                flag("if (true) { println() } else if (true) println() else println()", "if"(1)),
                flag("if (true) { println() } else if (true) println() else if (true) println()", "if"(1)),
                flag(
                    "if (true) { println() } else if (true) println() else if (true) println() else println()",
                    "if"(1)
                ),
            )

            @TestFactory
            fun `partially extra braces are flagged (last branch)`() = listOf(
                flag("if (true) println() else { println() }", "else"(1)),
                flag("if (true) println() else if (true) { println() }", "if"(2)),
                flag("if (true) println() else if (true) println() else { println() }", "else"(2)),
                flag("if (true) println() else if (true) println() else if (true) { println() }", "if"(3)),
                flag(
                    "if (true) println() else if (true) println() else if (true) println() else { println() }",
                    "else"(3)
                ),
            )

            @TestFactory
            fun `partially extra braces are flagged (middle branches)`() = listOf(
                flag("if (true) println() else if (true) { println() } else println()", "if"(2)),
                flag("if (true) println() else if (true) { println() } else if (true) println()", "if"(2)),
                flag(
                    "if (true) println() else if (true) { println() } else if (true) { println() } else println()",
                    "if"(2),
                    "if"(3)
                ),
            )

            @TestFactory fun `fully extra braces are flagged`() = listOf(
                flag("if (true) { println() }", "if"(1)),
                flag("if (true) { println() } else { println() }", "if"(1), "else"(1)),
                flag("if (true) { println() } else if (true) { println() }", "if"(1), "if"(2)),
                flag(
                    "if (true) { println() } else if (true) { println() } else { println() }",
                    "if"(1),
                    "if"(2),
                    "else"(2)
                ),
                flag(
                    "if (true) { println() } else if (true) { println() } else if (true) { println() }",
                    "if"(1),
                    "if"(2),
                    "if"(3)
                ),
                flag(
                    "if (true) { println() } else if (true) { println() } else if (true) { println() } else { println() }",
                    "if"(1),
                    "if"(2),
                    "if"(3),
                    "else"(3)
                ),
            )
        }

        @Nested
        inner class `=necessary` {

            private fun flag(code: String, vararg locations: (String) -> Pair<Int, Int>): DynamicNode {
                val validTopLevelCode = """
                    fun main() {
                        $code
                    }
                """.trimIndent()
                return testCombinations(BracePolicy.Necessary.config, NOT_RELEVANT, validTopLevelCode, *locations)
            }

            @TestFactory
            fun `no braces are accepted`() = listOf(
                flag("if (true) println()", *NOTHING),
                flag("if (true) println() else println()", *NOTHING),
                flag("if (true) println() else if (true) println()", *NOTHING),
                flag("if (true) println() else if (true) println() else println()", *NOTHING),
                flag("if (true) println() else if (true) println() else if (true) println()", *NOTHING),
                flag("if (true) println() else if (true) println() else if (true) println() else println()", *NOTHING),
            )

            @TestFactory
            fun `existing braces are flagged`() = listOf(
                flag("if (true) { println() }", "if"(1)),
                flag("if (true) { println() } else { println() }", "if"(1), "else"(1)),
                flag("if (true) { println() } else if (true) { println() }", "if"(1), "if"(2)),
                flag(
                    "if (true) { println() } else if (true) { println() } else { println() }",
                    "if"(1),
                    "if"(2),
                    "else"(2)
                ),
                flag(
                    "if (true) { println() } else if (true) { println() } else if (true) { println() }",
                    "if"(1),
                    "if"(2),
                    "if"(3)
                ),
                flag(
                    "if (true) { println() } else if (true) { println() } else if (true) { println() } else { println() }",
                    "if"(1),
                    "if"(2),
                    "if"(3),
                    "else"(3)
                ),
            )

            @TestFactory
            fun `partially extra braces are flagged (first branch)`() = listOf(
                flag("if (true) { println() }", "if"(1)),
                flag("if (true) { println() } else println()", "if"(1)),
                flag("if (true) { println() } else if (true) println()", "if"(1)),
                flag("if (true) { println() } else if (true) println() else println()", "if"(1)),
                flag("if (true) { println() } else if (true) println() else if (true) println()", "if"(1)),
                flag(
                    "if (true) { println() } else if (true) println() else if (true) println() else println()",
                    "if"(1)
                ),
            )

            @TestFactory
            fun `partially extra braces are flagged (last branch)`() = listOf(
                flag("if (true) println() else { println() }", "else"(1)),
                flag("if (true) println() else if (true) { println() }", "if"(2)),
                flag("if (true) println() else if (true) println() else { println() }", "else"(2)),
                flag("if (true) println() else if (true) println() else if (true) { println() }", "if"(3)),
                flag(
                    "if (true) println() else if (true) println() else if (true) println() else { println() }",
                    "else"(3)
                ),
            )

            @TestFactory
            fun `partially extra braces are flagged (middle branches)`() = listOf(
                flag("if (true) println() else if (true) { println() } else println()", "if"(2)),
                flag("if (true) println() else if (true) { println() } else if (true) println()", "if"(2)),
                flag(
                    "if (true) println() else if (true) { println() } else if (true) { println() } else println()",
                    "if"(2),
                    "if"(3),
                ),
            )

            @TestFactory
            fun `existing braces are not flagged when necessary`() = listOf(
                flag("if (true) { println(); println() }", *NOTHING),
                flag("if (true) println() else { println(); println() }", *NOTHING),
                flag("if (true) println() else if (true) { println(); println() }", *NOTHING),
                flag("if (true) println() else if (true) println() else { println(); println() }", *NOTHING),
            )

            @TestFactory
            fun `extra braces are flagged when not necessary (first)`() = listOf(
                flag("if (true) { println(); println() } else { println() }", "else"(1)),
                flag("if (true) { println(); println() } else if (true) { println() }", "if"(2)),
                flag("if (true) { println(); println() } else if (true) println() else { println() }", "else"(2)),
                flag("if (true) { println(); println() } else if (true) { println() } else println()", "if"(2)),
                flag(
                    "if (true) { println(); println() } else if (true) { println() } else { println() }",
                    "if"(2),
                    "else"(2)
                ),
            )

            @TestFactory
            fun `extra braces are flagged when not necessary (last)`() = listOf(
                flag("if (true) { println() } else { println(); println() }", "if"(1)),
                flag("if (true) { println() } else if (true) { println(); println() }", "if"(1)),
                flag("if (true) { println() } else if (true) println() else { println(); println() }", "if"(1)),
                flag("if (true) { println() } else if (true) println() else { println(); println() }", "if"(1)),
                flag("if (true) println() else if (true) { println() } else { println(); println() }", "if"(2)),
                flag(
                    "if (true) { println() } else if (true) { println() } else { println(); println() }",
                    "if"(1),
                    "if"(2)
                ),
            )
        }

        @Nested
        inner class `=consistent` {

            private fun flag(code: String, vararg locations: (String) -> Pair<Int, Int>): DynamicNode {
                val validTopLevelCode = """
                    fun main() {
                        $code
                    }
                """.trimIndent()
                return testCombinations(BracePolicy.Consistent.config, NOT_RELEVANT, validTopLevelCode, *locations)
            }

            @TestFactory
            fun `no braces are accepted`() = listOf(
                flag("if (true) println()", *NOTHING),
                flag("if (true) println() else println()", *NOTHING),
                flag("if (true) println() else if (true) println()", *NOTHING),
                flag("if (true) println() else if (true) println() else println()", *NOTHING),
                flag("if (true) println() else if (true) println() else if (true) println()", *NOTHING),
                flag("if (true) println() else if (true) println() else if (true) println() else println()", *NOTHING),
            )

            @TestFactory
            fun `existing braces are flagged`() = listOf(
                flag("if (true) { println() }", *NOTHING),
                flag("if (true) { println() } else { println() }", *NOTHING),
                flag("if (true) { println() } else if (true) { println() }", *NOTHING),
                flag("if (true) { println() } else if (true) { println() } else { println() }", *NOTHING),
                flag("if (true) { println() } else if (true) { println() } else if (true) { println() }", *NOTHING),
                flag(
                    "if (true) { println() } else if (true) { println() } else if (true) { println() } else { println() }",
                    *NOTHING
                ),
            )

            @TestFactory
            fun `partial braces are flagged (first branch)`() = listOf(
                flag("if (true) { println() }", *NOTHING),
                flag("if (true) { println() } else println()", "if"(1)),
                flag("if (true) { println() } else if (true) println()", "if"(1)),
                flag("if (true) { println() } else if (true) println() else println()", "if"(1)),
                flag("if (true) { println() } else if (true) println() else if (true) println()", "if"(1)),
                flag(
                    "if (true) { println() } else if (true) println() else if (true) println() else println()",
                    "if"(1)
                ),
            )

            @TestFactory
            fun `partial braces are flagged (last branch)`() = listOf(
                flag("if (true) println() else { println() }", "if"(1)),
                flag("if (true) println() else if (true) { println() }", "if"(1)),
                flag("if (true) println() else if (true) println() else { println() }", "if"(1)),
                flag("if (true) println() else if (true) println() else if (true) { println() }", "if"(1)),
                flag(
                    "if (true) println() else if (true) println() else if (true) println() else { println() }",
                    "if"(1),
                ),
            )

            @TestFactory
            fun `partial braces are flagged (middle branches)`() = listOf(
                flag("if (true) println() else if (true) { println() } else println()", "if"(1)),
                flag("if (true) println() else if (true) { println() } else if (true) println()", "if"(1)),
                flag(
                    "if (true) println() else if (true) { println() } else if (true) { println() } else println()",
                    "if"(1)
                ),
            )
        }
    }

    @Nested
    inner class multiLine {

        @Nested
        inner class `=always` {

            private fun flag(code: String, vararg locations: (String) -> Pair<Int, Int>): DynamicNode {
                val validTopLevelCode = """
                    fun main() {
                        $code
                    }
                """.trimIndent()
                return testCombinations(NOT_RELEVANT, BracePolicy.Always.config, validTopLevelCode, *locations)
            }

            @TestFactory
            fun `missing braces are flagged`() = listOf(
                flag(
                    """
                        if (true)
                            println()
                    """.trimIndent(),
                    "if"(1)
                ),
                flag(
                    """
                        if (true)
                            println()
                        else
                            println()
                    """.trimIndent(),
                    "if"(1),
                    "else"(1)
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true)
                            println()
                    """.trimIndent(),
                    "if"(1),
                    "if"(2)
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true)
                            println()
                        else
                            println()
                    """.trimIndent(),
                    "if"(1),
                    "if"(2),
                    "else"(2)
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true)
                            println()
                        else if (true)
                            println()
                    """.trimIndent(),
                    "if"(1),
                    "if"(2),
                    "if"(3)
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true)
                            println()
                        else if (true)
                            println()
                        else
                            println()
                    """.trimIndent(),
                    "if"(1),
                    "if"(2),
                    "if"(3),
                    "else"(3)
                ),
            )

            @TestFactory
            fun `existing braces are accepted`() = listOf(
                flag(
                    """
                        if (true) {
                            println()
                        }
                    """.trimIndent(),
                    *NOTHING
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else {
                            println()
                        }
                    """.trimIndent(),
                    *NOTHING
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true) {
                            println()
                        }
                    """.trimIndent(),
                    *NOTHING
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true) {
                            println()
                        } else {
                            println()
                        }
                    """.trimIndent(),
                    *NOTHING
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true) {
                            println()
                        } else if (true) {
                            println()
                        }
                    """.trimIndent(),
                    *NOTHING
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true) {
                            println()
                        } else if (true) {
                            println()
                        } else {
                            println()
                        }
                    """.trimIndent(),
                    *NOTHING
                ),
            )

            @TestFactory
            fun `partially missing braces are flagged (first branch)`() = listOf(
                flag(
                    """
                        if (true) {
                            println()
                        } else
                            println()
                    """.trimIndent(),
                    "else"(1)
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true)
                            println()
                    """.trimIndent(),
                    "if"(2)
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true)
                            println()
                        else
                            println()
                    """.trimIndent(),
                    "if"(2),
                    "else"(2)
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true)
                            println()
                        else if (true)
                            println()
                    """.trimIndent(),
                    "if"(2),
                    "if"(3)
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true)
                            println()
                        else if (true)
                            println()
                        else
                            println()
                    """.trimIndent(),
                    "if"(2),
                    "if"(3),
                    "else"(3)
                ),
            )

            @TestFactory
            fun `partially missing braces are flagged (last branch)`() = listOf(
                flag(
                    """
                        if (true)
                            println()
                        else {
                            println()
                        }
                    """.trimIndent(),
                    "if"(1)
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true) {
                            println()
                        }
                    """.trimIndent(),
                    "if"(1)
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true)
                            println()
                        else {
                            println()
                        }
                    """.trimIndent(),
                    "if"(1),
                    "if"(2)
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true)
                            println()
                        else if (true) {
                            println()
                        }
                    """.trimIndent(),
                    "if"(1),
                    "if"(2)
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true)
                            println()
                        else if (true)
                            println()
                        else {
                            println()
                        }
                    """.trimIndent(),
                    "if"(1),
                    "if"(2),
                    "if"(3)
                ),
            )

            @TestFactory
            fun `partially missing braces are flagged (middle branches)`() = listOf(
                flag(
                    """
                        if (true)
                            println()
                        else if (true) {
                            println()
                        } else
                            println()
                    """.trimIndent(),
                    "if"(1),
                    "else"(2)
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true) {
                            println()
                        } else if (true)
                            println()
                    """.trimIndent(),
                    "if"(1),
                    "if"(3)
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true) {
                            println()
                        } else if (true) {
                            println()
                        } else
                            println()
                    """.trimIndent(),
                    "if"(1),
                    "else"(3)
                ),
            )
        }

        @Nested
        inner class `=never` {

            private fun flag(code: String, vararg locations: (String) -> Pair<Int, Int>): DynamicNode {
                val validTopLevelCode = """
                    fun main() {
                        $code
                    }
                """.trimIndent()
                return testCombinations(NOT_RELEVANT, BracePolicy.Never.config, validTopLevelCode, *locations)
            }

            @TestFactory fun `no braces are accepted`() = listOf(
                flag(
                    """
                        if (true)
                            println()
                    """.trimIndent(),
                    *NOTHING
                ),
                flag(
                    """
                        if (true)
                            println()
                        else
                            println()
                    """.trimIndent(),
                    *NOTHING
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true)
                            println()
                    """.trimIndent(),
                    *NOTHING
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true)
                            println()
                        else
                            println()
                    """.trimIndent(),
                    *NOTHING
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true)
                            println()
                        else if (true)
                            println()
                    """.trimIndent(),
                    *NOTHING
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true)
                            println()
                        else if (true)
                            println()
                        else
                            println()
                    """.trimIndent(),
                    *NOTHING
                ),
            )

            @TestFactory
            fun `existing braces are flagged`() = listOf(
                flag(
                    """
                        if (true) {
                            println()
                        }
                    """.trimIndent(),
                    "if"(1)
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else {
                            println()
                        }
                    """.trimIndent(),
                    "if"(1),
                    "else"(1)
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true) {
                            println()
                        }
                    """.trimIndent(),
                    "if"(1),
                    "if"(2)
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true) {
                            println()
                        } else {
                            println()
                        }
                    """.trimIndent(),
                    "if"(1),
                    "if"(2),
                    "else"(2)
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true) {
                            println()
                        } else if (true) {
                            println()
                        }
                    """.trimIndent(),
                    "if"(1),
                    "if"(2),
                    "if"(3)
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true) {
                            println()
                        } else if (true) {
                            println()
                        } else {
                            println()
                        }
                    """.trimIndent(),
                    "if"(1),
                    "if"(2),
                    "if"(3),
                    "else"(3)
                ),
            )

            @TestFactory
            fun `partially extra braces are flagged (first branch)`() = listOf(
                flag(
                    """
                        if (true) {
                            println()
                        }
                    """.trimIndent(),
                    "if"(1)
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else
                            println()
                    """.trimIndent(),
                    "if"(1)
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true)
                            println()
                    """.trimIndent(),
                    "if"(1)
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true)
                            println()
                        else
                            println()
                    """.trimIndent(),
                    "if"(1)
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true)
                            println()
                        else if (true)
                            println()
                    """.trimIndent(),
                    "if"(1)
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true)
                            println()
                        else if (true)
                            println()
                        else
                            println()
                    """.trimIndent(),
                    "if"(1)
                ),
            )

            @TestFactory
            fun `partially extra braces are flagged (last branch)`() = listOf(
                flag(
                    """
                        if (true)
                            println()
                        else {
                            println()
                        }
                    """.trimIndent(),
                    "else"(1)
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true) {
                            println()
                        }
                    """.trimIndent(),
                    "if"(2)
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true)
                            println()
                        else {
                            println()
                        }
                    """.trimIndent(),
                    "else"(2)
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true)
                            println()
                        else if (true) {
                            println()
                        }
                    """.trimIndent(),
                    "if"(3)
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true)
                            println()
                        else if (true)
                            println()
                        else {
                            println()
                        }
                    """.trimIndent(),
                    "else"(3)
                ),
            )

            @TestFactory
            fun `partially extra braces are flagged (middle branches)`() = listOf(
                flag(
                    """
                        if (true)
                            println()
                        else if (true) {
                            println()
                        } else
                            println()
                    """.trimIndent(),
                    "if"(2)
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true) {
                            println()
                        } else if (true)
                            println()
                    """.trimIndent(),
                    "if"(2)
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true) {
                            println()
                        } else if (true) {
                            println()
                        } else println()
                    """.trimIndent(),
                    "if"(2),
                    "if"(3)
                ),
            )

            @TestFactory fun `fully extra braces are flagged`() = listOf(
                flag(
                    """
                        if (true) {
                            println()
                        }
                    """.trimIndent(),
                    "if"(1)
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else {
                            println()
                        }
                    """.trimIndent(),
                    "if"(1),
                    "else"(1)
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true) {
                            println()
                        }
                    """.trimIndent(),
                    "if"(1),
                    "if"(2)
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true) {
                            println()
                        } else {
                            println()
                        }
                    """.trimIndent(),
                    "if"(1),
                    "if"(2),
                    "else"(2)
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true) {
                            println()
                        } else if (true) {
                            println()
                        }
                    """.trimIndent(),
                    "if"(1),
                    "if"(2),
                    "if"(3)
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true) {
                            println()
                        } else if (true) {
                            println()
                        } else {
                            println()
                        }
                    """.trimIndent(),
                    "if"(1),
                    "if"(2),
                    "if"(3),
                    "else"(3)
                ),
            )
        }

        @Nested
        inner class `=necessary` {

            private fun flag(code: String, vararg locations: (String) -> Pair<Int, Int>): DynamicNode {
                val validTopLevelCode = """
                    fun main() {
                        $code
                    }
                """.trimIndent()
                return testCombinations(NOT_RELEVANT, BracePolicy.Necessary.config, validTopLevelCode, *locations)
            }

            @TestFactory
            fun `no braces are accepted`() = listOf(
                flag(
                    """
                        if (true)
                            println()
                    """.trimIndent(),
                    *NOTHING
                ),
                flag(
                    """
                        if (true)
                            println()
                        else
                            println()
                    """.trimIndent(),
                    *NOTHING
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true)
                            println()
                    """.trimIndent(),
                    *NOTHING
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true)
                            println()
                        else
                            println()
                    """.trimIndent(),
                    *NOTHING
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true)
                            println()
                        else if (true)
                            println()
                    """.trimIndent(),
                    *NOTHING
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true)
                            println()
                        else if (true)
                            println()
                        else
                            println()
                    """.trimIndent(),
                    *NOTHING
                ),
            )

            @TestFactory
            fun `existing braces are flagged`() = listOf(
                flag(
                    """
                        if (true) {
                            println()
                        }
                    """.trimIndent(),
                    "if"(1)
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else {
                            println()
                        }
                    """.trimIndent(),
                    "if"(1),
                    "else"(1)
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true) {
                            println()
                        }
                    """.trimIndent(),
                    "if"(1),
                    "if"(2)
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true) {
                            println()
                        } else {
                            println()
                        }
                    """.trimIndent(),
                    "if"(1),
                    "if"(2),
                    "else"(2)
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true) {
                            println()
                        } else if (true) {
                            println()
                        }
                    """.trimIndent(),
                    "if"(1),
                    "if"(2),
                    "if"(3)
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true) {
                            println()
                        } else if (true) {
                            println()
                        } else {
                            println()
                        }
                    """.trimIndent(),
                    "if"(1),
                    "if"(2),
                    "if"(3),
                    "else"(3)
                ),
            )

            @TestFactory
            fun `partially extra braces are flagged (first branch)`() = listOf(
                flag(
                    """
                        if (true) {
                            println()
                        }
                    """.trimIndent(),
                    "if"(1)
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else
                            println()
                    """.trimIndent(),
                    "if"(1)
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true)
                            println()
                    """.trimIndent(),
                    "if"(1)
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true)
                            println()
                        else
                            println()
                    """.trimIndent(),
                    "if"(1)
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true)
                            println()
                        else if (true)
                            println()
                    """.trimIndent(),
                    "if"(1)
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true)
                            println()
                        else if (true)
                            println()
                        else
                            println()
                    """.trimIndent(),
                    "if"(1)
                ),
            )

            @TestFactory
            fun `partially extra braces are flagged (last branch)`() = listOf(
                flag(
                    """
                        if (true)
                            println()
                        else {
                            println()
                        }
                    """.trimIndent(),
                    "else"(1)
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true) {
                            println()
                        }
                    """.trimIndent(),
                    "if"(2)
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true)
                            println()
                        else {
                            println()
                        }
                    """.trimIndent(),
                    "else"(2)
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true)
                            println()
                        else if (true) {
                            println()
                        }
                    """.trimIndent(),
                    "if"(3)
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true)
                            println()
                        else if (true)
                            println()
                        else {
                            println()
                        }
                    """.trimIndent(),
                    "else"(3)
                ),
            )

            @TestFactory
            fun `partially extra braces are flagged (middle branches)`() = listOf(
                flag(
                    """
                        if (true)
                            println()
                        else if (true) {
                            println()
                        } else
                            println()
                    """.trimIndent(),
                    "if"(2)
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true) {
                            println()
                        } else if (true)
                            println()
                    """.trimIndent(),
                    "if"(2)
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true) {
                            println()
                        } else if (true) {
                            println()
                        } else
                            println()
                    """.trimIndent(),
                    "if"(2),
                    "if"(3)
                ),
            )

            @TestFactory
            fun `existing braces are not flagged when necessary`() = listOf(
                flag(
                    """
                        if (true) {
                            println()
                            println()
                        }
                    """.trimIndent(),
                    *NOTHING
                ),
                flag(
                    """
                        if (true)
                            println()
                        else {
                            println()
                            println()
                        }
                    """.trimIndent(),
                    *NOTHING
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true) {
                            println()
                            println()
                        }
                    """.trimIndent(),
                    *NOTHING
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true)
                            println()
                        else {
                            println()
                            println()
                        }
                    """.trimIndent(),
                    *NOTHING
                ),
            )

            @TestFactory
            fun `extra braces are flagged when not necessary (first)`() = listOf(
                flag(
                    """
                        if (true) {
                            println()
                            println()
                        } else {
                            println()
                        }
                    """.trimIndent(),
                    "else"(1)
                ),
                flag(
                    """
                        if (true) {
                            println()
                            println()
                        } else if (true) {
                            println()
                        }
                    """.trimIndent(),
                    "if"(2)
                ),
                flag(
                    """
                        if (true) {
                            println()
                            println()
                        } else if (true)
                            println()
                        else {
                            println()
                        }
                    """.trimIndent(),
                    "else"(2)
                ),
                flag(
                    """
                        if (true) {
                            println()
                            println()
                        } else if (true) {
                            println()
                        } else
                            println()
                    """.trimIndent(),
                    "if"(2)
                ),
                flag(
                    """
                        if (true) {
                            println()
                            println()
                        } else if (true) {
                            println()
                        } else {
                            println()
                        }
                    """.trimIndent(),
                    "if"(2),
                    "else"(2)
                ),
            )

            @TestFactory
            fun `extra braces are flagged when not necessary (last)`() = listOf(
                flag(
                    """
                        if (true) {
                            println()
                        } else {
                            println()
                            println()
                        }
                    """.trimIndent(),
                    "if"(1)
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true) {
                            println()
                            println()
                        }
                    """.trimIndent(),
                    "if"(1)
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true)
                            println()
                        else {
                            println()
                            println()
                        }
                    """.trimIndent(),
                    "if"(1)
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true)
                            println()
                        else {
                            println()
                            println()
                        }
                    """.trimIndent(),
                    "if"(1)
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true) {
                            println()
                        } else {
                            println()
                            println()
                        }
                    """.trimIndent(),
                    "if"(2)
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true) {
                            println()
                        } else {
                            println()
                            println()
                        }
                    """.trimIndent(),
                    "if"(1),
                    "if"(2)
                ),
            )
        }

        @Nested
        inner class `=consistent` {

            private fun flag(code: String, vararg locations: (String) -> Pair<Int, Int>): DynamicNode {
                val validTopLevelCode = """
                    fun main() {
                        $code
                    }
                """.trimIndent()
                return testCombinations(NOT_RELEVANT, BracePolicy.Consistent.config, validTopLevelCode, *locations)
            }

            @TestFactory
            fun `no braces are accepted`() = listOf(
                flag(
                    """
                        if (true)
                            println()
                    """.trimIndent(),
                    *NOTHING
                ),
                flag(
                    """
                        if (true)
                            println()
                        else
                            println()
                    """.trimIndent(),
                    *NOTHING
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true)
                            println()
                    """.trimIndent(),
                    *NOTHING
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true)
                            println()
                        else
                            println()
                    """.trimIndent(),
                    *NOTHING
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true)
                            println()
                        else if (true)
                            println()
                    """.trimIndent(),
                    *NOTHING
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true)
                            println()
                        else if (true)
                            println()
                        else
                            println()
                    """.trimIndent(),
                    *NOTHING
                ),
            )

            @TestFactory
            fun `existing braces are flagged`() = listOf(
                flag(
                    """
                        if (true) {
                            println()
                        }
                    """.trimIndent(),
                    *NOTHING
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else {
                            println()
                        }
                    """.trimIndent(),
                    *NOTHING
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true) {
                            println()
                        }
                    """.trimIndent(),
                    *NOTHING
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true) {
                            println()
                        } else {
                            println()
                        }
                    """.trimIndent(),
                    *NOTHING
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true) {
                            println()
                        } else if (true) {
                            println()
                        }
                    """.trimIndent(),
                    *NOTHING
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true) {
                            println()
                        } else if (true) {
                            println()
                        } else {
                            println()
                        }
                    """.trimIndent(),
                    *NOTHING
                ),
            )

            @TestFactory
            fun `partial braces are flagged (first branch)`() = listOf(
                flag(
                    """
                        if (true) {
                            println()
                        }
                    """.trimIndent(),
                    *NOTHING
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else
                            println()
                    """.trimIndent(),
                    "if"(1)
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true)
                            println()
                    """.trimIndent(),
                    "if"(1)
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true)
                            println()
                        else
                            println()
                    """.trimIndent(),
                    "if"(1)
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true)
                            println()
                        else if (true)
                            println()
                    """.trimIndent(),
                    "if"(1)
                ),
                flag(
                    """
                        if (true) {
                            println()
                        } else if (true)
                            println()
                        else if (true)
                            println()
                        else
                            println()
                    """.trimIndent(),
                    "if"(1)
                ),
            )

            @TestFactory
            fun `partial braces are flagged (last branch)`() = listOf(
                flag(
                    """
                        if (true)
                            println()
                        else {
                            println()
                        }
                    """.trimIndent(),
                    "if"(1)
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true) {
                            println()
                        }
                    """.trimIndent(),
                    "if"(1)
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true)
                            println()
                        else {
                            println()
                        }
                    """.trimIndent(),
                    "if"(1)
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true)
                            println()
                        else if (true) {
                            println()
                        }
                    """.trimIndent(),
                    "if"(1)
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true)
                            println()
                        else if (true)
                            println()
                        else {
                            println()
                        }
                    """.trimIndent(),
                    "if"(1),
                ),
            )

            @TestFactory
            fun `partial braces are flagged (middle branches)`() = listOf(
                flag(
                    """
                        if (true)
                            println()
                        else if (true) {
                            println()
                        } else
                            println()
                    """.trimIndent(),
                    "if"(1)
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true) {
                            println()
                        } else if (true)
                            println()
                    """.trimIndent(),
                    "if"(1)
                ),
                flag(
                    """
                        if (true)
                            println()
                        else if (true) {
                            println()
                        } else if (true) {
                            println()
                        } else
                            println()
                    """.trimIndent(),
                    "if"(1)
                ),
            )

            @TestFactory
            fun `no braces in chained expression are accepted`() = listOf(
                flag(
                    """
                        if (true) {
                            ""
                        } else if (true) {
                            ""
                        } else {
                            ""
                        }.trim()
                    """.trimIndent(),
                    *NOTHING
                ),
                flag(
                    """
                        if (true) {
                            ""
                        } else if (true) {
                            ""
                        } else {
                            ""
                        }?.trim()
                    """.trimIndent(),
                    *NOTHING
                ),
                flag(
                    """
                        if (true) {
                            ""
                        } else if (true) {
                            ""
                        } else {
                            ""
                        } ?: ""
                    """.trimIndent(),
                    *NOTHING
                ),
                flag(
                    """
                        if (true) {
                            true
                        } else if (true) {
                            true
                        } else {
                            true
                        } && true
                    """.trimIndent(),
                    *NOTHING
                ),
                flag(
                    """
                        if (true) {
                            1
                        } else if (true) {
                            1
                        } else {
                            1
                        } or 1
                    """.trimIndent(),
                    *NOTHING
                ),
            )
        }
    }

    @Nested
    inner class nested {

        @TestFactory
        fun `nested ifs are flagged for consistency`() = testCombinations(
            singleLine = NOT_RELEVANT,
            multiLine = BracePolicy.Consistent.config,
            code = """
                fun main() {
                    if (true) {
                        if (true) {
                            println()
                        } else println()
                    } else println()
                }
            """.trimIndent(),
            locations = arrayOf(
                "if"(1),
                "if"(2),
            ),
        )

        @TestFactory
        fun `only multiline ifs are are flagged (complex)`() = testCombinations(
            singleLine = BracePolicy.Never.config,
            multiLine = BracePolicy.Always.config,
            code = """
                fun main() {
                    if (if (true) true else false)
                        if (true) true else false
                    else
                        println(if (true) true else false)
                }
            """.trimIndent(),
            locations = arrayOf(
                "if"(1),
                "else"(3),
            ),
        )

        @TestFactory
        fun `only multiline ifs are are flagged (simple)`() = testCombinations(
            singleLine = BracePolicy.Never.config,
            multiLine = BracePolicy.Always.config,
            code = """
                fun main() {
                    if (true)
                        if (true) true else false
                }
            """.trimIndent(),
            locations = arrayOf(
                "if"(1),
            ),
        )

        @TestFactory
        fun `nested ifs are flagged for always`() = testCombinations(
            singleLine = BracePolicy.Always.config,
            multiLine = BracePolicy.Always.config,
            code = """
                fun main() {
                    if (if (true) true else false)
                        if (true) println() else println()
                    else
                        println(if (true) true else false)
                }
            """.trimIndent(),
            locations = arrayOf(
                "if"(1),
                "if"(2),
                "else"(1),
                "if"(3),
                "else"(2),
                "else"(3),
                "if"(4),
                "else"(4),
            ),
        )

        @TestFactory
        fun `nested ifs inside condition are flagged for always`() = testCombinations(
            singleLine = BracePolicy.Always.config,
            multiLine = NOT_RELEVANT,
            code = """
                fun main() {
                    if (if (if (true) true else false) true else false) println()
                }
            """.trimIndent(),
            locations = arrayOf(
                "if"(1),
                "if"(2),
                "if"(3),
                "else"(1),
                "else"(2),
            ),
        )

        @TestFactory
        fun `nested ifs inside then are flagged for always`() = testCombinations(
            singleLine = BracePolicy.Always.config,
            multiLine = NOT_RELEVANT,
            code = """
                fun main() {
                    if (true) if (true) if (true) println() else println() else println() else println()
                }
            """.trimIndent(),
            locations = arrayOf(
                "if"(1),
                "if"(2),
                "if"(3),
                "else"(1),
                "else"(2),
                "else"(3),
            ),
        )
    }

    @TestFactory
    fun `whens are not flagged`() = testCombinations(
        singleLine = NOT_RELEVANT,
        multiLine = NOT_RELEVANT,
        code = """
            fun main() {
                when (true) {
                    true -> println()
                    else -> println()
                }
            }
        """.trimIndent(),
        locations = NOTHING
    )

    companion object {

        /**
         * Not relevant means, that it should be covered, but for that specific test case the value doesn't matter.
         * This needs to be covered still, to make sure it never becomes relevant.
         * In this rule, configuration options should be separate for each config, not coupled with each other.
         */
        private const val NOT_RELEVANT: String = "*"

        /**
         * Nothing is expected to be flagged as a finding.
         */
        private val NOTHING: Array<(String) -> Pair<Int, Int>> = emptyArray()

        private fun createSubject(singleLine: String, multiLine: String): BracesOnIfStatements {
            val config = TestConfig(
                "singleLine" to singleLine,
                "multiLine" to multiLine,
            )
            return BracesOnIfStatements(config)
        }

        private fun options(option: String): List<String> =
            if (option == NOT_RELEVANT) {
                BracePolicy.entries.map { it.config }
            } else {
                listOf(option)
            }

        private fun testCombinations(
            singleLine: String,
            multiLine: String,
            code: String,
            vararg locations: (String) -> Pair<Int, Int>,
        ): DynamicNode {
            val codeLocation = locations.map { it(code) }.toTypedArray()
            // Separately compile the code because otherwise all the combinations would compile them again and again.
            val compileTest = dynamicTest("Compiles: $code") {
                BracesOnIfStatements(Config.empty).lint(code)
            }
            val validationTests = createBraceTests(singleLine, multiLine) { rule ->
                rule.test(code, *codeLocation)
            }
            val locationString = if (NOTHING.contentEquals(codeLocation)) {
                "nothing"
            } else {
                codeLocation.map { TextLocation(it.first, it.second) }.toString()
            }
            return dynamicContainer("flags $locationString in `$code`", validationTests + compileTest)
        }

        private fun BracesOnIfStatements.test(code: String, vararg locations: Pair<Int, Int>) {
            // This creates a 10 character prefix (signature/9, space/1) for every code example.
            // Note: not compileAndLint for performance reasons, compilation is in a separate test.
            val findings = lint("fun f() { $code }", compile = false)
            // Offset text locations by the above prefix, it results in 0-indexed locations.
            val offset = 10
            val textPositions = locations.map { it.first + offset to it.second + offset }
            assertThat(findings).satisfiesExactlyInAnyOrder(
                *textPositions
                    .map { textPosition -> ThrowingConsumer<Finding> { assertThat(it).hasTextLocation(textPosition) } }
                    .toTypedArray()
            )
        }

        /**
         * Generates a list of tests for the given brace policy combinations.
         * The expectations in the test will be the same for all combinations.
         *
         * @see options for how the arguments are interpreted.
         */
        private fun createBraceTests(
            singleLine: String,
            multiLine: String,
            test: (BracesOnIfStatements) -> Unit,
        ): List<DynamicNode> {
            val singleOptions = options(singleLine)
            val multiOptions = options(multiLine)
            require(singleOptions.isNotEmpty() && multiOptions.isNotEmpty()) {
                "No options to test: $singleLine -> $singleOptions, $multiLine -> $multiOptions"
            }
            return singleOptions.flatMap { singleLineOption ->
                multiOptions.map { multiLineOption ->
                    val trace = Exception("Async stack trace of TestFactory")
                    dynamicTest("singleLine=$singleLineOption, multiLine=$multiLineOption") {
                        try {
                            // Note: if you jumped here from a failed test's stack trace,
                            // select the "Async stack trace of TestFactory" cause's
                            // last relevant stack line to jump to the actual test code.
                            test(createSubject(singleLineOption, multiLineOption))
                        } catch (e: Throwable) {
                            generateSequence(e) { it.cause }.last().initCause(trace)
                            throw e
                        }
                    }
                }
            }
        }

        operator fun String.invoke(ordinal: Int): (String) -> Pair<Int, Int> =
            { code ->
                fun String.next(string: String, start: Int): Int? = indexOf(string, start).takeIf { it != -1 }

                val indices = generateSequence(code.next(this, 0)) { startIndex ->
                    code.next(this, startIndex + 1)
                }
                val index = requireNotNull(indices.elementAtOrNull(ordinal - 1)) {
                    "There's no $ordinal. occurrence of '$this' in '$code'"
                }
                index to index + this.length
            }
    }
}
