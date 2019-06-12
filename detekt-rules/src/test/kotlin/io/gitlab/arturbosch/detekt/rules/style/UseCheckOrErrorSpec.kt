package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UseCheckOrErrorSpec : Spek({

    val subject by memoized { UseCheckOrError(Config.empty) }

    describe("UseCheckOrError rule") {

        it("reports if a an IllegalStateException is thrown") {
            val code = """
                fun x() {
                    doSomething()
                    if (a < 0) throw IllegalStateException()
                }"""
            assertThat(subject.lint(code)).hasSize(1)
        }

        it("reports if a an IllegalStateException is thrown with an error message") {
            val code = """
                fun x() {
                    doSomething()
                    if (a < 0) throw IllegalStateException("More details")
                }"""
            assertThat(subject.lint(code)).hasSize(1)
        }

        it("reports if a an IllegalStateException is thrown as default case of a when expression") {
            val code = """
                fun x(a: Int) =
                    when (a) {
                        1 -> doSomething()
                        else -> throw IllegalStateException()
                    }"""
            assertThat(subject.lint(code)).hasSize(1)
        }

        it("reports if an IllegalStateException is thrown by its fully qualified name") {
            val code = """
                fun x() {
                    doSomething()
                    if (a < 0) throw java.lang.IllegalStateException()
                }"""
            assertThat(subject.lint(code)).hasSize(1)
        }

        it("reports if an IllegalStateException is thrown by its fully qualified name using the kotlin type alias") {
            val code = """
                fun x() {
                    doSomething()
                    if (a < 0) throw kotlin.IllegalStateException()
                }"""
            assertThat(subject.lint(code)).hasSize(1)
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

        it("does not report an issue if the exception thrown unconditionally") {
            val code = """fun doThrow() = throw IllegalStateException("message")"""
            assertThat(subject.lint(code)).hasSize(0)
        }

        it("does not report an issue if the exception thrown unconditionally in a function block") {
            val code = """fun doThrow() { throw IllegalStateException("message") }"""
            assertThat(subject.lint(code)).hasSize(0)
        }

        context("throw is not after a precondition"){

            it("does not report an issue if the exception is after a block") {
                val code = """
                    fun doSomethingOrThrow(test: Int): Int {
                        var index = 0
                        repeat(test){
                            if (Math.random() == 1.0) {
                                return it
                            }
                        }
                        throw IllegalStateException("Test was too big")
                    }""".trimIndent()
                assertThat(subject.lint(code)).isEmpty()
            }

            it("does not report an issue if the exception is after a elvis operator") {
                val code = """
                    fun tryToCastOrThrow(list: List<*>) : LinkedList<*> {
                        val subclass = list as? LinkedList
                            ?: throw IllegalStateException("List is not a LinkedList")
                        return subclass
                    }""".trimIndent()
                assertThat(subject.lint(code)).isEmpty()
            }
        }
    }
})
