package dev.detekt.rules.exceptions

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class SwallowedExceptionSpec {
    val subject = SwallowedException(Config.empty)

    @Test
    fun `reports a swallowed exception`() {
        val code = """
            fun f() {
                try {
                } catch (e: Exception) {
                    throw IllegalArgumentException()
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports swallowed exceptions only using exception strings`() {
        val code = """
            fun f() {
                try {
                } catch (e: IllegalStateException) {
                    throw IllegalArgumentException(e.message)
                } catch (f: Exception) {
                    throw Exception(IllegalArgumentException(f.toString()))
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(2)
    }

    @Test
    fun `reports swallowed exceptions only using exception strings via variables`() {
        val code = """
            fun f() {
                try {
                } catch (e: IllegalStateException) {
                    val message = e.message
                    throw IllegalArgumentException(message)
                } catch (f: Exception) {
                    val message = f.toString()
                    throw Exception(IllegalArgumentException(message))
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(2)
    }

    @Test
    fun `reports swallowed exceptions only using exception strings via variables in 'if' block`() {
        val code = """
            fun f() {
                try {
                } catch (e: IllegalStateException) {
                    if (true) {
                        val message = e.message
                        throw IllegalArgumentException(message)
                    }
                } catch (f: Exception) {
                    val message = f.toString()
                    if (true) {
                        throw Exception(IllegalArgumentException(message))
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(2)
    }

    @Test
    fun `reports swallowed exceptions when it has multiple throw expressions`() {
        val code = """
            fun f(condition: Boolean) {
                try {
                    println()
                } catch (e: IllegalStateException) {
                    if (condition) {
                        throw IllegalArgumentException(e.message)
                    }
                    throw IllegalArgumentException(e)
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports swallowed exceptions when it has multiple throw expressions 2`() {
        val code = """
            fun f(condition: Boolean) {
                try {
                    println()
                } catch (e: IllegalStateException) {
                    if (condition) {
                        throw IllegalArgumentException(e)
                    }
                    throw IllegalArgumentException(e.message)
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports nested swallowed exceptions`() {
        val code = """
            fun f(condition: Boolean) {
                try {
                    println()
                } catch (e: IllegalStateException) {
                    try {
                    } catch (nested: Exception) {
                        throw IllegalArgumentException()
                    }
                    throw IllegalArgumentException(e)
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports a swallowed exception that is not logged`() {
        val code = """
            fun f() {
                try {
                } catch (e: Exception) {
                    println()
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Nested
    inner class `when given listOf(IllegalArgumentException) ignores given exception types config` {
        val ignoredExceptionValue = listOf("IllegalArgumentException")

        val config =
            TestConfig("ignoredExceptionTypes" to ignoredExceptionValue)

        val rule = SwallowedException(config)

        @Test
        fun `ignores given exception type in configuration`() {
            val code = """
                fun f() {
                    try {
                    } catch (e: IllegalArgumentException) {
                    }
                }
            """.trimIndent()
            assertThat(rule.lint(code)).isEmpty()
        }

        @Test
        fun `reports exception type that is missing in the configuration`() {
            val code = """
                fun f() {
                    try {
                    } catch (e: Exception) {
                    }
                }
            """.trimIndent()
            assertThat(rule.lint(code)).hasSize(1)
        }
    }

    @Nested
    inner class `when given IllegalArgumentException ignores given exception types config` {
        val rule = SwallowedException(TestConfig("ignoredExceptionTypes" to listOf("IllegalArgumentException")))

        @Test
        fun `ignores given exception type in configuration`() {
            val code = """
                fun f() {
                    try {
                    } catch (e: IllegalArgumentException) {
                    }
                }
            """.trimIndent()
            assertThat(rule.lint(code)).isEmpty()
        }

        @Test
        fun `reports exception type that is missing in the configuration`() {
            val code = """
                fun f() {
                    try {
                    } catch (e: Exception) {
                    }
                }
            """.trimIndent()
            assertThat(rule.lint(code)).hasSize(1)
        }
    }

    @Nested
    inner class `ignores given exception name config` {

        val rule = SwallowedException(TestConfig("allowedExceptionNameRegex" to "myIgnore"))

        @Test
        fun `ignores given exception name`() {
            val code = """
                fun f() {
                    try {
                    } catch (myIgnore: IllegalArgumentException) {
                    }
                }
            """.trimIndent()
            assertThat(rule.lint(code)).isEmpty()
        }

        @Test
        fun `reports exception name`() {
            val code = """
                fun f() {
                    try {
                    } catch (e: IllegalArgumentException) {
                    }
                }
            """.trimIndent()
            assertThat(rule.lint(code)).hasSize(1)
        }
    }

    @Test
    fun `does not report wrapped exceptions`() {
        val code = """
            fun f() {
                try {
                } catch (e: IllegalStateException) {
                    throw IllegalArgumentException(e.message, e)
                } catch (e: Exception) {
                    throw IllegalArgumentException(e)
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report used exception variables`() {
        val code = """
            fun f() {
                try {
                } catch (e: IllegalArgumentException) {
                    print(e)
                } catch (e: Exception) {
                    print(e.message)
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report when an exception is used as a receiver and the return value is thrown`() {
        val code = """
            fun Exception.transformException(): Exception {
               return this
            }
            
            fun test() {
               try {
               } catch (e: Exception) {
                  throw e.transformException()
               }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @ParameterizedTest(name = "ignores {0} in the catch clause by default")
    @MethodSource("dev.detekt.rules.exceptions.SwallowedException#getEXCEPTIONS_IGNORED_BY_DEFAULT")
    fun `ignores $exceptionName in the catch clause by default`(exceptionName: String) {
        val code = """
            import java.net.MalformedURLException
            import java.text.ParseException
            
            fun f() {
                try {
                } catch (e: $exceptionName) {
                    throw Exception()
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @ParameterizedTest(name = "ignores {0} in the catch body by default")
    @MethodSource("dev.detekt.rules.exceptions.SwallowedException#getEXCEPTIONS_IGNORED_BY_DEFAULT")
    fun `ignores $exceptionName in the catch body by default`(exceptionName: String) {
        val exceptionInstantiation = if (exceptionName == "ParseException") {
            "$exceptionName(\"\", 0)"
        } else {
            "$exceptionName(\"\")"
        }

        val code = """
            import java.net.MalformedURLException
            import java.text.ParseException
            
            fun f() {
                try {
                } catch (e: Exception) {
                    throw $exceptionInstantiation
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }
}
