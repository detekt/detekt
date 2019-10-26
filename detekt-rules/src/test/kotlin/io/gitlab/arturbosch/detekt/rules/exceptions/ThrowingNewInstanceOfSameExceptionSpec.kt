package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ThrowingNewInstanceOfSameExceptionSpec : Spek({
    val subject by memoized { ThrowingNewInstanceOfSameException() }

    describe("ThrowingNewInstanceOfSameException rule") {

        context("a catch block which rethrows a new instance of the caught exception") {
            val code = """
            fun x() {
                try {
                } catch (e: IllegalStateException) {
                    throw IllegalStateException(e)
                }
            }
        """

            it("should report") {
                val findings = subject.compileAndLint(code)
                assertThat(findings).hasSize(1)
            }
        }

        context("a catch block which rethrows a new instance of another exception") {
            val code = """
            fun x() {
                try {
                } catch (e: IllegalStateException) {
                    throw IllegalArgumentException(e)
                }
            }
        """

            it("should not report") {
                val findings = subject.compileAndLint(code)
                assertThat(findings).isEmpty()
            }
        }

        context("a catch block which throws a new instance of the same exception type without wrapping the caught exception") {
            val code = """
            fun x() {
                try {
                } catch (e: IllegalStateException) {
                    throw IllegalStateException()
                }
            }
        """

            it("should not report") {
                val findings = subject.compileAndLint(code)
                assertThat(findings).isEmpty()
            }
        }
    }
})
