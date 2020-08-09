package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.net.MalformedURLException
import java.text.ParseException

class SwallowedExceptionSpec : Spek({
    val subject by memoized { SwallowedException() }

    describe("SwallowedException rule") {

        it("reports a swallowed exception") {
            val code = """
                fun f() {
                    try {
                    } catch (e: Exception) {
                        throw IllegalArgumentException()
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports swallowed exceptions only using exception strings") {
            val code = """
                fun f() {
                    try {
                    } catch (e: IllegalStateException) {
                        throw IllegalArgumentException(e.message)
                    } catch (f: Exception) {
                        throw Exception(IllegalArgumentException(f.toString()))
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(2)
        }

        it("reports swallowed exceptions only using exception strings via variables") {
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
            """
            assertThat(subject.compileAndLint(code)).hasSize(2)
        }

        it("reports swallowed exceptions only using exception strings via variables in 'if' block") {
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
            """
            assertThat(subject.compileAndLint(code)).hasSize(2)
        }

        it("reports swallowed exceptions when it has multiple throw expressions") {
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
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports swallowed exceptions when it has multiple throw expressions 2") {
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
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports a swallowed exception that is not logged") {
            val code = """
                fun f() {
                    try {
                    } catch (e: Exception) {
                        println()
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        listOf(listOf("IllegalArgumentException"), "IllegalArgumentException").forEach { ignoredExceptionValue ->
            context("ignores given exception types config") {

                val config by memoized {
                    TestConfig(SwallowedException.IGNORED_EXCEPTION_TYPES to ignoredExceptionValue)
                }
                val rule by memoized { SwallowedException(config) }

                it("ignores given exception type in configuration") {
                    val code = """
                    fun f() {
                        try {
                        } catch (e: IllegalArgumentException) {
                        }
                    }
                """
                    assertThat(rule.compileAndLint(code)).isEmpty()
                }

                it("reports exception type that is missing in the configuration") {
                    val code = """
                    fun f() {
                        try {
                        } catch (e: Exception) {
                        }
                    }
                """
                    assertThat(rule.compileAndLint(code)).hasSize(1)
                }
            }
        }

        context("ignores given exception name config") {

            val config by memoized { TestConfig(mapOf(SwallowedException.ALLOWED_EXCEPTION_NAME_REGEX to "myIgnore")) }
            val rule by memoized { SwallowedException(config) }

            it("ignores given exception name") {
                val code = """
                    fun f() {
                        try {
                        } catch (myIgnore: IllegalArgumentException) {
                        }
                    }
                """
                assertThat(rule.compileAndLint(code)).isEmpty()
            }

            it("reports exception name") {
                val code = """
                    fun f() {
                        try {
                        } catch (e: IllegalArgumentException) {
                        }
                    }
                """
                assertThat(rule.compileAndLint(code)).hasSize(1)
            }
        }

        it("does not report wrapped exceptions") {
            val code = """
                fun f() {
                    try {
                    } catch (e: IllegalStateException) {
                        throw IllegalArgumentException(e.message, e)
                    } catch (e: Exception) {
                        throw IllegalArgumentException(e)
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report used exception variables") {
            val code = """
                fun f() {
                    try {
                    } catch (e: IllegalArgumentException) {
                        print(e)
                    } catch (e: Exception) {
                        print(e.message)
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        listOf(
            InterruptedException::class.java.simpleName,
            MalformedURLException::class.java.simpleName,
            NumberFormatException::class.java.simpleName,
            ParseException::class.java.simpleName
        ).forEach { exceptionName ->
            it("ignores $exceptionName by default") {
                val code = """
                import java.net.MalformedURLException
                import java.text.ParseException

                fun f() {
                    try {
                    } catch (e: $exceptionName) {
                        throw Exception()
                    }
                }
            """
                assertThat(subject.compileAndLint(code)).isEmpty()
            }
        }
    }
})
