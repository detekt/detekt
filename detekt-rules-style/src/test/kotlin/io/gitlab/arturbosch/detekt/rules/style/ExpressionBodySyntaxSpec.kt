package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ExpressionBodySyntaxSpec : Spek({
    val subject by memoized { ExpressionBodySyntax(Config.empty) }

    describe("ExpressionBodySyntax rule") {

        context("several return statements") {

            it("reports constant return") {
                assertThat(subject.compileAndLint("""
                    fun stuff(): Int {
                        return 5
                    }
                """
                )).hasSize(1)
            }

            it("reports return statement with method chain") {
                assertThat(subject.compileAndLint("""
                    fun stuff(): String {
                        return StringBuilder().append(0).toString()
                    }
                """
                )).hasSize(1)
            }

            it("reports return statements with conditionals") {
                assertThat(subject.compileAndLint("""
                    fun stuff(): Int {
                        return if (true) return 5 else return 3
                    }
                    fun stuff(): Int {
                        return try { return 5 } catch (e: Exception) { return 3 }
                    }
                """)).hasSize(2)
            }

            it("does not report multiple if statements") {
                assertThat(subject.compileAndLint("""
                    fun stuff(): Boolean {
                        if (true) return true
                        return false
                    }
                """)).isEmpty()
            }

            it("does not report when using shortcut return") {
                assertThat(subject.compileAndLint("""
                    fun caller(): String {
                        return callee("" as String? ?: return "")
                    }

                    fun callee(a: String): String = ""
                """)).isEmpty()
            }
        }

        context("several return statements with multiline method chain") {

            val code = """
                fun stuff(): String {
                    return StringBuilder()
                        .append(1)
                        .toString()
                }"""

            it("does not report with the default configuration") {
                assertThat(subject.compileAndLint(code)).isEmpty()
            }

            it("reports with includeLineWrapping = true configuration") {
                val config = TestConfig(mapOf(ExpressionBodySyntax.INCLUDE_LINE_WRAPPING to "true"))
                assertThat(ExpressionBodySyntax(config).compileAndLint(code)).hasSize(1)
            }
        }

        context("several return statements with multiline when expression") {

            val code = """
                fun stuff(arg: Int): Int {
                    return when(arg) {
                        0 -> 0
                        else -> 1
                    }
                }"""

            it("does not report with the default configuration") {
                assertThat(subject.compileAndLint(code)).isEmpty()
            }

            it("reports with includeLineWrapping = true configuration") {
                val config = TestConfig(mapOf(ExpressionBodySyntax.INCLUDE_LINE_WRAPPING to "true"))
                assertThat(ExpressionBodySyntax(config).compileAndLint(code)).hasSize(1)
            }
        }

        context("several return statements with multiline if expression") {

            val code = """
                fun stuff(arg: Int): Int {
                    return if (arg == 0) 0
                    else 1
                }
            """

            it("does not report with the default configuration") {
                assertThat(subject.compileAndLint(code)).isEmpty()
            }

            it("reports with includeLineWrapping = true configuration") {
                val config = TestConfig(mapOf(ExpressionBodySyntax.INCLUDE_LINE_WRAPPING to "true"))
                assertThat(ExpressionBodySyntax(config).compileAndLint(code)).hasSize(1)
            }
        }
    }
})
