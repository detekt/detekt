package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class ReturnFromFinallySpec(val env: KotlinCoreEnvironment) {

    val subject = ReturnFromFinally(Config.empty)

    inner class `a finally block with a return statement` {
        val code = """
            fun x() {
                try {
                } finally {
                    return
                }
            }
        """.trimIndent()

        @Test
        fun `should report`() {
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }
    }

    @Nested
    inner class `a finally block with no return statement` {
        val code = """
            fun x() {
                try {
                } finally {
                }
            }
        """.trimIndent()

        @Test
        fun `should not report`() {
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `a finally block with a nested return statement` {
        val code = """
            fun x() {
                try {
                } finally {
                    if (1 == 1) {
                        return
                    }
                }
            }
        """.trimIndent()

        @Test
        fun `should report`() {
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }
    }

    @Nested
    inner class `a finally block with a return in an inner function` {
        val code = """
            fun x() {
                try {
                } finally {
                    fun y() {
                        return
                    }
                    y()
                }
            }
        """.trimIndent()

        @Test
        fun `should not report`() {
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `a finally block with a return as labelled expression` {
        val code = """
            fun x() {
                label@{
                    try {
                    } finally {
                        return@label
                    }
                }
            }
        """.trimIndent()

        @Test
        fun `should report when ignoreLabeled is false`() {
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should not report when ignoreLabeled is true`() {
            val config = TestConfig("ignoreLabeled" to "true")
            val findings = ReturnFromFinally(config).compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `a finally block as expression for property` {
        @Test
        fun `should report`() {
            val code = """
                val expression = try {
                    "try"
                } catch (e: Exception) {
                    "exception"
                } finally {
                    "finally"
                }
            """.trimIndent()

            val finding = subject.compileAndLintWithContext(env, code)

            assertThat(finding).hasSize(1)
        }
    }

    @Nested
    inner class `a finally block as expression for method` {
        @Test
        fun `should report`() {
            val code = """
                fun expression() = try {
                    "try"
                } catch (e: Exception) {
                    "exception"
                } finally {
                    "finally"
                }
            """.trimIndent()

            val finding = subject.compileAndLintWithContext(env, code)

            assertThat(finding).hasSize(1)
        }
    }

    @Nested
    inner class `when a finally block called method that return value` {
        @Test
        fun `should report`() {
            val code = """
                fun expression() = try {
                    "try"
                } catch (e: Exception) {
                    "exception"
                } finally {
                    compute()
                }
                
                fun compute(): String = "value"
            """.trimIndent()

            val finding = subject.compileAndLintWithContext(env, code)

            assertThat(finding).hasSize(1)
        }
    }

    @Nested
    inner class `when finally block absents in expression for property` {
        @Test
        fun `shouldn't report`() {
            val code = """
                val expression = try {
                    "try"
                } catch (e: Exception) {
                    "exception"
                }
            """.trimIndent()

            val finding = subject.compileAndLintWithContext(env, code)

            assertThat(finding).isEmpty()
        }
    }

    @Nested
    inner class `when finally block absents in expression for method` {

        @Test
        fun `shouldn't report`() {
            val code = """
                fun expression() = try {
                    "try"
                } catch (e: Exception) {
                    "exception"
                }
            """.trimIndent()

            val finding = subject.compileAndLintWithContext(env, code)

            assertThat(finding).isEmpty()
        }
    }

    @Nested
    inner class `when try catch finally block is independent` {
        @Test
        fun `shouldn't report`() {
            val code = """
                fun expression() {
                    try {
                        "try"
                    } catch (e: Exception) {
                        "exception"
                    } finally {
                        "finally"
                    }
                }
            """.trimIndent()

            val finding = subject.compileAndLintWithContext(env, code)

            assertThat(finding).isEmpty()
        }
    }

    @Nested
    inner class `when finally block doesn't contain return value` {
        @Test
        fun `shouldn't report`() {
            val code = """
                val expression = try {
                    "try"
                } catch (e: Exception) {
                    "exception"
                } finally {
                    println("finally")
                }
            """.trimIndent()

            val finding = subject.compileAndLintWithContext(env, code)

            assertThat(finding).isEmpty()
        }
    }

    @Nested
    inner class `when return value in finally block is property` {
        @Test
        fun `should report`() {
            val code = """
                val property: String = "property"
                val expression = try {
                    "try"
                } catch (e: Exception) {
                    "exception"
                } finally {
                    println("finally")
                    property
                }
            """.trimIndent()

            val finding = subject.compileAndLintWithContext(env, code)

            assertThat(finding).hasSize(1)
        }
    }
}
