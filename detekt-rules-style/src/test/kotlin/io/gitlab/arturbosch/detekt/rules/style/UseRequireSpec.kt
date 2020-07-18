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

class UseRequireSpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { UseRequire(Config.empty) }

    describe("UseRequire rule") {

        it("reports if a precondition throws an IllegalArgumentException") {
            val code = """
                fun x(a: Int) {
                    if (a < 0) throw IllegalArgumentException()
                    doSomething()
                }"""
            assertThat(subject.lint(code)).hasSourceLocation(2, 16)
        }

        it("reports if a precondition throws an IllegalArgumentException with more details") {
            val code = """
                fun x(a: Int) {
                    if (a < 0) throw IllegalArgumentException("More details")
                    doSomething()
                }"""
            assertThat(subject.lint(code)).hasSourceLocation(2, 16)
        }

        it("reports if a precondition throws a fully qualified IllegalArgumentException") {
            val code = """
                fun x(a: Int) {
                    if (a < 0) throw java.lang.IllegalArgumentException()
                    doSomething()
                }"""
            assertThat(subject.lint(code)).hasSourceLocation(2, 16)
        }

        it("reports if a precondition throws a fully qualified IllegalArgumentException using the kotlin type alias") {
            val code = """
                fun x(a: Int) {
                    if (a < 0) throw kotlin.IllegalArgumentException()
                    doSomething()
                }"""
            assertThat(subject.lint(code)).hasSourceLocation(2, 16)
        }

        it("does not report if a precondition throws a different kind of exception") {
            val code = """
                fun x(a: Int) {
                    if (a < 0) throw SomeBusinessException()
                    doSomething()
                }"""
            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report an issue if the exception thrown has a message and a cause") {
            val code = """
                private fun x(a: Int): Nothing {
                    doSomething()
                    throw IllegalArgumentException("message", cause)
                }"""
            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report an issue if the exception thrown as the only action in a block") {
            val code = """
                fun unsafeRunSync(): A =
                    foo.fold({ throw IllegalArgumentException("message") }, ::identity)"""
            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report an issue if the exception thrown unconditionally") {
            val code = """fun doThrow() = throw IllegalArgumentException("message")"""
            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report an issue if the exception thrown unconditionally in a function block") {
            val code = """fun doThrow() { throw IllegalArgumentException("message") }"""
            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report if the exception thrown has a non-String argument") {
            val code = """
                fun test(throwable: Throwable) {
                    if (throwable !is NumberFormatException) throw IllegalArgumentException(throwable)
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report if the exception thrown has a String literal argument and a non-String argument") {
            val code = """
                fun test(throwable: Throwable) {
                    if (throwable !is NumberFormatException) throw IllegalArgumentException("a", throwable)
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report if the exception thrown has a non-String literal argument") {
            val code = """
                fun test(throwable: Throwable) {
                    val s = ""
                    if (throwable !is NumberFormatException) throw IllegalArgumentException(s)
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        context("with binding context") {

            it("does not report if the exception thrown has a non-String argument") {
                val code = """
                    fun test(throwable: Throwable) {
                        if (throwable !is NumberFormatException) throw IllegalArgumentException(throwable)
                    }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            it("does not report if the exception thrown has a String literal argument and a non-String argument") {
                val code = """
                    fun test(throwable: Throwable) {
                        if (throwable !is NumberFormatException) throw IllegalArgumentException("a", throwable)
                    }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            it("reports if the exception thrown has a non-String literal argument") {
                val code = """
                    fun test(throwable: Throwable) {
                        val s = ""
                        if (throwable !is NumberFormatException) throw IllegalArgumentException(s)
                    }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
            }
            it("reports if the exception thrown has a String literal argument") {
                val code = """
                    fun test(throwable: Throwable) {
                        if (throwable !is NumberFormatException) throw IllegalArgumentException("a")
                    }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
            }
        }

        context("throw is not after a precondition") {

            it("does not report an issue if the exception is inside a when") {
                val code = """
                    fun whenOrThrow(item : List<*>) = when(item) {
                        is ArrayList<*> -> 1
                        is LinkedList<*> -> 2
                        else -> throw IllegalArgumentException("Not supported List type")
                    }
                    """
                assertThat(subject.lint(code)).isEmpty()
            }

            it("does not report an issue if the exception is after a block") {
                val code = """
                    fun doSomethingOrThrow(test: Int): Int {
                        var index = 0
                        repeat(test){
                            if (Math.random() == 1.0) {
                                return it
                            }
                        }
                        throw IllegalArgumentException("Test was too big")
                    }"""
                assertThat(subject.lint(code)).isEmpty()
            }

            it("does not report an issue if the exception is after a elvis operator") {
                val code = """
                    fun tryToCastOrThrow(list: List<*>) : LinkedList<*> {
                        val subclass = list as? LinkedList
                            ?: throw IllegalArgumentException("List is not a LinkedList")
                        return subclass
                    }"""
                assertThat(subject.lint(code)).isEmpty()
            }
        }
    }
})
