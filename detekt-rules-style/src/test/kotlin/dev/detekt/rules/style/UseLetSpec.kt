package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lint
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class UseLetSpec {

    val subject = UseLet(Config.Empty)

    @TestFactory
    fun `it forbids all != null else null combinations`(): Iterable<DynamicTest> {
        val conditions = listOf(
            Triple("1 == null", false, true),
            Triple("null == 1", false, true),
            Triple("1 == 1", false, false),
            Triple("null == null", false, true),
            Triple("1 != null", true, false),
            Triple("null != 1", true, false),
            Triple("1 != 1", false, false),
            Triple("null != null", true, false),
        )

        val exprs = listOf(
            "1" to false,
            "null" to true,
            "{ 1 }" to false,
            "{ null }" to true,
        )

        return conditions.flatMap { (condition, isNonNullCheck, isNullCheck) ->
            exprs.flatMap { (left, leftIsNull) ->
                exprs.map { (right, rightIsNull) ->
                    DynamicTest.dynamicTest("($condition) $left else $right") {
                        val expr = "fun test() = if ($condition) $left else $right"
                        val shouldFail = (isNonNullCheck && rightIsNull) || (isNullCheck && leftIsNull)
                        val findings = subject.lint(expr)
                        if (shouldFail) {
                            assertThat(findings).singleElement()
                                .hasMessage(subject.description)
                        } else {
                            assertThat(findings).isEmpty()
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `do not capture blocks that have multiple expressions`() {
        val findings = subject.lint(
            """
                fun testCallToCreateTempFile(s: String?) {
                    val x = if (s == null) {
                        println("foo")
                        null
                    } else {
                        "x"
                    }
                }
            """.trimIndent()
        )

        assertThat(findings).isEmpty()
    }

    @Test
    fun `it allows the following expressions (currently)`() {
        val findings = subject.lint(
            """
                fun testCallToCreateTempFile() {
                    val x: String? = "abc"
                    if (x == null) println(x) else null
                    if (x is String) println(x)
                    if (x != null) { println(x) }
                }
            """.trimIndent()
        )

        assertThat(findings).isEmpty()
    }
}
