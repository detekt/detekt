package dev.detekt.rules.exceptions

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.junit.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class ReturnFromFinallySpec(val env: KotlinEnvironmentContainer) {

    val subject = ReturnFromFinally(Config.empty)

    @Nested
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
            val findings = subject.lintWithContext(env, code)
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
            val findings = subject.lintWithContext(env, code)
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
            val findings = subject.lintWithContext(env, code)
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
            val findings = subject.lintWithContext(env, code)
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
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should not report when ignoreLabeled is true`() {
            val config = TestConfig("ignoreLabeled" to true)
            val findings = ReturnFromFinally(config).lintWithContext(env, code)
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

            val finding = subject.lintWithContext(env, code)

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

            val finding = subject.lintWithContext(env, code)

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

            val finding = subject.lintWithContext(env, code)

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

            val finding = subject.lintWithContext(env, code)

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

            val finding = subject.lintWithContext(env, code)

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

            val finding = subject.lintWithContext(env, code)

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

            val finding = subject.lintWithContext(env, code)

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

            val finding = subject.lintWithContext(env, code)

            assertThat(finding).hasSize(1)
        }
    }

    @Test
    fun `should report for inlined try block returning a non-Unit type`() {
        val code = """
            fun tidyUpButReturnInt(): Int {
                println("Tidying up...")
                return 3
            }
            
            fun foo(): Int = try {
                1 + 2
            } finally {
                tidyUpButReturnInt()
            }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `shouldn't report for inlined try block returning Unit`() {
        val code = """
            fun doSomething(): Unit = println("I am busy")
            fun tidyUp(): Unit = println("Cleaning up after myself, but returning Unit")

            fun bar(): Unit = try {
                doSomething()
            } finally {
                tidyUp()
            }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }
}
