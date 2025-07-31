package dev.detekt.rules.exceptions

import dev.detekt.api.Config
import dev.detekt.test.lint
import dev.detekt.test.location
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RethrowCaughtExceptionSpec {
    val subject = RethrowCaughtException(Config.empty)

    @Test
    fun `reports when the same exception is rethrown`() {
        val code = """
            fun f() {
                try {
                } catch (e: IllegalStateException) {
                    throw e
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `does not report when the other exception is rethrown with same name`() {
        val code = """
            class A {
                private lateinit var e: Exception
                fun f() {
                    try {
                    } catch (e: IllegalStateException) {
                        throw this.e
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `reports when the same exception succeeded by dead code is rethrown`() {
        val code = """
            fun f() {
                try {
                } catch (e: IllegalStateException) {
                    throw e
                    print("log")
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports when the same nested exception is rethrown`() {
        val code = """
            fun f() {
                try {
                } catch (outer: IllegalStateException) {
                    try {
                    } catch (inner: IllegalStateException) {
                        throw inner
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `does not report a wrapped exception`() {
        val code = """
            fun f() {
                try {
                } catch (e: IllegalStateException) {
                    throw IllegalArgumentException(e)
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report wrapped exceptions`() {
        val code = """
            fun f() {
                try {
                } catch (e: IllegalStateException) {
                    throw IllegalArgumentException(e)
                } catch (f: Exception) {
                    throw IllegalArgumentException("msg", f)
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report logged exceptions`() {
        val code = """
            fun f() {
                try {
                } catch (e: IllegalStateException) {
                    print(e)
                } catch (f: Exception) {
                    print(f)
                    throw IllegalArgumentException("msg", f)
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report when taking specific actions before throwing the exception`() {
        val code = """
            fun f() {
                try {
                } catch (e: IllegalStateException) {
                    print("log") // taking specific action before throwing the exception
                    throw e
                }
                try {
                } catch (e: IllegalStateException) {
                    print(e.message) // taking specific action before throwing the exception
                    throw e
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report when exception rethrown only in first catch`() {
        val code = """
            fun f() {
                try {
                } catch (e: IllegalStateException) {
                    throw e
                } catch (e: Exception) {
                    print(e)
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report when some work is done in last catch`() {
        val code = """
            fun f() {
                try {
                } catch (e: IllegalStateException) {
                    throw e
                } catch (e: Exception) {
                    print(e)
                    throw e
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report when there is no catch clauses`() {
        val code = """
            fun f() {
                try {
                } finally {
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `reports when exception rethrown in last catch`() {
        val code = """
            fun f() {
                try {
                } catch (e: IllegalStateException) {
                    print(e)
                } catch (e: Exception) {
                    throw e
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports 2 violations for each catch`() {
        val code = """
            fun f() {
                try {
                } catch (e: IllegalStateException) {
                    throw e
                } catch (e: Exception) {
                    // some comment
                    throw e
                }
            }
        """.trimIndent()
        val result = subject.lint(code)
        assertThat(result).hasSize(2)
        // ensure correct violation order
        assertThat(result[0].location.source.line == 4).isTrue
        assertThat(result[1].location.source.line == 7).isTrue
    }
}
