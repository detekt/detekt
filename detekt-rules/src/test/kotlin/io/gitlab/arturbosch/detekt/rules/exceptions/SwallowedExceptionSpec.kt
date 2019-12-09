package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.TestConfig
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

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

        context("ignores given exception types config") {

            val config = TestConfig(mapOf(SwallowedException.IGNORED_EXCEPTION_TYPES to "IllegalArgumentException"))
            val rule = SwallowedException(config)

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

        context("ignores given exception name config") {

            val config = TestConfig(mapOf(SwallowedException.ALLOWED_EXCEPTION_NAME_REGEX to "myIgnore"))
            val rule = SwallowedException(config)

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
    }
})
