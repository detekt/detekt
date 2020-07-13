package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ReturnFromFinallySpec : Spek({
    val subject by memoized { ReturnFromFinally() }

    describe("ReturnFromFinally rule") {

        context("a finally block with a return statement") {
            val code = """
            fun x() {
                try {
                } finally {
                    return
                }
            }
        """

            it("should report") {
                val findings = subject.compileAndLint(code)
                assertThat(findings).hasSize(1)
            }
        }

        context("a finally block with no return statement") {
            val code = """
            fun x() {
                try {
                } finally {
                }
            }
        """

            it("should not report") {
                val findings = subject.compileAndLint(code)
                assertThat(findings).isEmpty()
            }
        }

        context("a finally block with a nested return statement") {
            val code = """
            fun x() {
                try {
                } finally {
                    if (1 == 1) {
                        return
                    }
                }
            }
        """

            it("should report") {
                val findings = subject.compileAndLint(code)
                assertThat(findings).hasSize(1)
            }
        }

        context("a finally block with a return in an inner function") {
            val code = """
            fun x() {
                try {
                } finally {
                    fun y() {
                        return
                    }
                    y()
                }
            }
        """

            it("should not report") {
                val findings = subject.compileAndLint(code)
                assertThat(findings).isEmpty()
            }
        }

        context("a finally block with a return as labelled expression") {
            val code = """
            fun x() {
                try {
                } finally {
                    label@{
                     return@label
                    }
                }
            }
        """
            it("should report when ignoreLabeled is false") {
                val findings = subject.compileAndLint(code)
                assertThat(findings).hasSize(1)
            }

            it("should not report when ignoreLabeled is true") {
                val config = TestConfig(mapOf(ReturnFromFinally.IGNORE_LABELED to "true"))
                val findings = ReturnFromFinally(config).compileAndLint(code)
                assertThat(findings).isEmpty()
            }
        }
    }
})
