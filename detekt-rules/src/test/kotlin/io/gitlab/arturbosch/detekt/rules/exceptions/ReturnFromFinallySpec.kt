package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions
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
                val findings = subject.lint(code)
                Assertions.assertThat(findings).hasSize(1)
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
                val findings = subject.lint(code)
                Assertions.assertThat(findings).hasSize(0)
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
                val findings = subject.lint(code)
                Assertions.assertThat(findings).hasSize(1)
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
                val findings = subject.lint(code)
                Assertions.assertThat(findings).hasSize(0)
            }
        }
    }
})
