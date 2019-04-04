package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UseRequireSpec : Spek({

    val subject by memoized { UseRequire(Config.empty) }

    describe("UseRequire rule") {

        it("reports if a precondition throws an IllegalArgumentException") {
            val code = """
                fun x(a: Int) {
                    if (a < 0) throw IllegalArgumentException()
                    doSomething()
                }"""
            assertThat(subject.lint(code)).hasSize(1)
        }

        it("reports if a precondition throws an IllegalArgumentException with more details") {
            val code = """
                fun x(a: Int) {
                    if (a < 0) throw IllegalArgumentException("More details")
                    doSomething()
                }"""
            assertThat(subject.lint(code)).hasSize(1)
        }

        it("reports if a precondition throws a fully qualified IllegalArgumentException") {
            val code = """
                fun x(a: Int) {
                    if (a < 0) throw java.lang.IllegalArgumentException()
                    doSomething()
                }"""
            assertThat(subject.lint(code)).hasSize(1)
        }

        it("reports if a precondition throws a fully qualified IllegalArgumentException using the kotlin type alias") {
            val code = """
                fun x(a: Int) {
                    if (a < 0) throw kotlin.IllegalArgumentException()
                    doSomething()
                }"""
            assertThat(subject.lint(code)).hasSize(1)
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
    }
})
