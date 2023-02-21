package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private const val INCLUDE_LINE_WRAPPING = "includeLineWrapping"

class ExpressionBodySyntaxSpec {
    val subject = ExpressionBodySyntax(Config.empty)

    @Nested
    inner class `several return statements` {

        @Test
        fun `reports constant return`() {
            assertThat(
                subject.compileAndLint(
                    """
                fun stuff(): Int {
                    return 5
                }
                    """.trimIndent()
                )
            ).hasSize(1)
        }

        @Test
        fun `reports return statement with method chain`() {
            assertThat(
                subject.compileAndLint(
                    """
                fun stuff(): String {
                    return StringBuilder().append(0).toString()
                }
                    """.trimIndent()
                )
            ).hasSize(1)
        }

        @Test
        fun `reports return statements with conditionals`() {
            assertThat(
                subject.compileAndLint(
                    """
                fun stuff(): Int {
                    return if (true) return 5 else return 3
                }
                fun stuff(): Int {
                    return try { return 5 } catch (e: Exception) { return 3 }
                }
                    """.trimIndent()
                )
            ).hasSize(2)
        }

        @Test
        fun `does not report multiple if statements`() {
            assertThat(
                subject.compileAndLint(
                    """
                fun stuff(): Boolean {
                    if (true) return true
                    return false
                }
                    """.trimIndent()
                )
            ).isEmpty()
        }

        @Test
        fun `does not report when using shortcut return`() {
            assertThat(
                subject.compileAndLint(
                    """
                fun caller(): String {
                    return callee("" as String? ?: return "")
                }
                
                fun callee(a: String): String = ""
                    """.trimIndent()
                )
            ).isEmpty()
        }
    }

    @Nested
    inner class `several return statements with multiline method chain` {

        val code = """
            fun stuff(): String {
                return StringBuilder()
                    .append(1)
                    .toString()
            }
        """.trimIndent()

        @Test
        fun `does not report with the default configuration`() {
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `reports with includeLineWrapping = true configuration`() {
            val config = TestConfig(mapOf(INCLUDE_LINE_WRAPPING to "true"))
            assertThat(ExpressionBodySyntax(config).compileAndLint(code)).hasSize(1)
        }
    }

    @Nested
    inner class `several return statements with multiline when expression` {

        val code = """
            fun stuff(arg: Int): Int {
                return when(arg) {
                    0 -> 0
                    else -> 1
                }
            }
        """.trimIndent()

        @Test
        fun `does not report with the default configuration`() {
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `reports with includeLineWrapping = true configuration`() {
            val config = TestConfig(mapOf(INCLUDE_LINE_WRAPPING to "true"))
            assertThat(ExpressionBodySyntax(config).compileAndLint(code)).hasSize(1)
        }
    }

    @Nested
    inner class `several return statements with multiline if expression` {

        val code = """
            fun stuff(arg: Int): Int {
                return if (arg == 0) 0
                else 1
            }
        """.trimIndent()

        @Test
        fun `does not report with the default configuration`() {
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `reports with includeLineWrapping = true configuration`() {
            val config = TestConfig(mapOf(INCLUDE_LINE_WRAPPING to "true"))
            assertThat(ExpressionBodySyntax(config).compileAndLint(code)).hasSize(1)
        }
    }
}
