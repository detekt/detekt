package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class RethrowCaughtExceptionSpec : Spek({
    val subject by memoized { RethrowCaughtException() }

    describe("RethrowCaughtException rule") {

        it("reports when the same exception is rethrown") {
            val code = """
                fun f() {
                    try {
                    } catch (e: IllegalStateException) {
                        throw e
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports when the same exception succeeded by dead code is rethrown") {
            val code = """
                fun f() {
                    try {
                    } catch (e: IllegalStateException) {
                        throw e
                        print("log")
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports when the same nested exception is rethrown") {
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
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("does not report a wrapped exception") {
            val code = """
                fun f() {
                    try {
                    } catch (e: IllegalStateException) {
                        throw IllegalArgumentException(e)
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report wrapped exceptions") {
            val code = """
                fun f() {
                    try {
                    } catch (e: IllegalStateException) {
                        throw IllegalArgumentException(e)
                    } catch (f: Exception) {
                        throw IllegalArgumentException("msg", f)
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report logged exceptions") {
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
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report when taking specific actions before throwing the exception") {
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
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
})
