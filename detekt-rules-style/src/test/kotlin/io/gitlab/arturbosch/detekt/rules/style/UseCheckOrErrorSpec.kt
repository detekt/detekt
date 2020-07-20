package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import io.gitlab.arturbosch.detekt.test.lint
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UseCheckOrErrorSpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { UseCheckOrError(Config.empty) }

    describe("UseCheckOrError rule") {

        it("reports if a an IllegalStateException is thrown") {
            val code = """
                fun x() {
                    doSomething()
                    if (a < 0) throw IllegalStateException()
                }"""
            assertThat(subject.lint(code)).hasSourceLocation(3, 16)
        }

        it("reports if a an IllegalStateException is thrown with an error message") {
            val code = """
                fun x() {
                    doSomething()
                    if (a < 0) throw IllegalStateException("More details")
                }"""
            assertThat(subject.lint(code)).hasSourceLocation(3, 16)
        }

        it("reports if a an IllegalStateException is thrown as default case of a when expression") {
            val code = """
                fun x(a: Int) =
                    when (a) {
                        1 -> doSomething()
                        else -> throw IllegalStateException()
                    }"""
            assertThat(subject.lint(code)).hasSourceLocation(4, 17)
        }

        it("reports if an IllegalStateException is thrown by its fully qualified name") {
            val code = """
                fun x() {
                    doSomething()
                    if (a < 0) throw java.lang.IllegalStateException()
                }"""
            assertThat(subject.lint(code)).hasSourceLocation(3, 16)
        }

        it("reports if an IllegalStateException is thrown by its fully qualified name using the kotlin type alias") {
            val code = """
                fun x() {
                    doSomething()
                    if (a < 0) throw kotlin.IllegalStateException()
                }"""
            assertThat(subject.lint(code)).hasSourceLocation(3, 16)
        }

        it("does not report if any other kind of exception is thrown") {
            val code = """
                fun x() {
                    doSomething()
                    if (a < 0) throw SomeBusinessException()
                }"""
            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report an issue if the exception thrown has a message and a cause") {
            val code = """
                private fun missing(): Nothing {
                    if  (cause != null) {
                        throw IllegalStateException("message", cause)
                    }
                }"""
            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report an issue if the exception thrown as the only action in a block") {
            val code = """
                fun unsafeRunSync(): A =
                    unsafeRunTimed(Duration.INFINITE)
                        .fold({ throw IllegalStateException("message") }, ::identity)"""
            assertThat(subject.lint(code)).isEmpty()
        }

        it("reports an issue if the exception thrown as the only action in a function") {
            val code = """fun doThrow() = throw IllegalStateException("message")"""
            assertThat(subject.lint(code)).hasSourceLocation(1, 17)
        }

        it("reports an issue if the exception thrown as the only action in a function block") {
            val code = """fun doThrow() { throw IllegalStateException("message") }"""
            assertThat(subject.lint(code)).hasSourceLocation(1, 17)
        }

        it("does not report if the exception thrown has a non-String argument") {
            val code = """
                fun test(throwable: Throwable) {
                    when(throwable) {
                        is NumberFormatException -> println("a")
                        else -> throw IllegalStateException(throwable)
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report if the exception thrown has a String literal argument and a non-String argument") {
            val code = """
                fun test(throwable: Throwable) {
                    when(throwable) {
                        is NumberFormatException -> println("a")
                        else -> throw IllegalStateException("b", throwable)
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report if the exception thrown has a non-String literal argument") {
            val code = """
                fun test(throwable: Throwable) {
                    when(throwable) {
                        is NumberFormatException -> println("a")
                        else -> throw IllegalStateException(throwable.toString())
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        context("with binding context") {

            it("does not report if the exception thrown has a non-String argument") {
                val code = """
                    fun test(throwable: Throwable) {
                        when(throwable) {
                            is NumberFormatException -> println("a")
                            else -> throw IllegalStateException(throwable)
                        }
                    }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            it("does not report if the exception thrown has a String literal argument and a non-String argument") {
                val code = """
                    fun test(throwable: Throwable) {
                        when(throwable) {
                            is NumberFormatException -> println("a")
                            else -> throw IllegalStateException("b", throwable)
                        }
                    }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            it("reports if the exception thrown has a non-String literal argument") {
                val code = """
                    fun test(throwable: Throwable) {
                        when(throwable) {
                            is NumberFormatException -> println("a")
                            else -> throw IllegalStateException(throwable.toString())
                        }
                    }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
            }

            it("reports if the exception thrown has a string literal argument") {
                val code = """
                    fun test(throwable: Throwable) {
                        when(throwable) {
                            is NumberFormatException -> println("a")
                            else -> throw IllegalStateException("b")
                        }
                    }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
            }
        }
    }
})
