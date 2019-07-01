package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ThrowingExceptionsWithoutMessageOrCauseSpec : Spek({
    val subject by memoized {
        ThrowingExceptionsWithoutMessageOrCause(
                TestConfig(mapOf(ThrowingExceptionsWithoutMessageOrCause.EXCEPTIONS to "IllegalArgumentException"))
        )
    }

    describe("ThrowingExceptionsWithoutMessageOrCause rule") {

        context("several exception calls") {

            val code = """
				fun x() {
					IllegalArgumentException(IllegalArgumentException())
					IllegalArgumentException("foo")
					throw IllegalArgumentException()
				}"""

            it("reports calls to the default constructor") {
                assertThat(subject.compileAndLint(code)).hasSize(2)
            }

            it("does not report calls to the default constructor with empty configuration") {
                val config = TestConfig(mapOf(ThrowingExceptionsWithoutMessageOrCause.EXCEPTIONS to ""))
                val findings = ThrowingExceptionsWithoutMessageOrCause(config).compileAndLint(code)
                assertThat(findings).hasSize(0)
            }
        }

        context("a test code which asserts an exception") {

            it("does not report a call to this exception") {
                val code = """
				fun test() {
					org.assertj.core.api.Assertions.assertThatIllegalArgumentException().isThrownBy { println() }
				}
			"""
                assertThat(subject.compileAndLint(code)).isEmpty()
            }
        }
    }
})
