package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private const val INCLUDE_LINE_WRAPPING = "includeLineWrapping"

class ExpressionBodySyntaxSpec {
    val subject = ExpressionBodySyntax(Config.Empty)

    @Nested
    inner class `several return statements` {

        @Test
        fun `reports constant return`() {
            assertThat(
                subject.lint(
                    """
                        fun stuff(): Int {
                            return 5
                        }
                    """.trimIndent()
                )
            ).hasSize(1)
        }

        @Test
        fun `reports return statements in property getter and setter`() {
            val code = """
                class Test {
                    var b: Boolean
                        get() {
                            return true
                        }
                        set(value) {
                            return
                        }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(2)
        }

        @Test
        fun `does not report properties with no getter or setter body`() {
            val code = """
                class Test {
                    var b1: Boolean = false
                        get
                        set
                    var b2: Boolean = false
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `reports return statement with method chain`() {
            assertThat(
                subject.lint(
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
                subject.lint(
                    """
                        fun stuff(): Int {
                            return if (true) return 5 else return 3
                        }
                        fun stuff2(): Int {
                            return try { return 5 } catch (e: Exception) { return 3 }
                        }
                    """.trimIndent()
                )
            ).hasSize(2)
        }

        @Test
        fun `does not report multiple if statements`() {
            assertThat(
                subject.lint(
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
                subject.lint(
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
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `reports with includeLineWrapping = true configuration`() {
            val config = TestConfig(INCLUDE_LINE_WRAPPING to true)
            assertThat(ExpressionBodySyntax(config).lint(code)).hasSize(1)
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
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `reports with includeLineWrapping = true configuration`() {
            val config = TestConfig(INCLUDE_LINE_WRAPPING to true)
            assertThat(ExpressionBodySyntax(config).lint(code)).hasSize(1)
        }
    }

    @Nested
    inner class `multiple return in when expression` {
        val code = """
            fun stuff(): Pair<String, String>? {
                return Pair(
                    first = "",
                    second = when {
                        true -> ""
                        else -> return null 
                    }
                )
            }
        """.trimIndent()

        @Test
        fun `does not report with the default configuration`() {
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report with includeLineWrapping = true configuration`() {
            val config = TestConfig(INCLUDE_LINE_WRAPPING to true)
            assertThat(ExpressionBodySyntax(config).lint(code)).isEmpty()
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
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `reports with includeLineWrapping = true configuration`() {
            val config = TestConfig(INCLUDE_LINE_WRAPPING to true)
            assertThat(ExpressionBodySyntax(config).lint(code)).hasSize(1)
        }
    }
}
