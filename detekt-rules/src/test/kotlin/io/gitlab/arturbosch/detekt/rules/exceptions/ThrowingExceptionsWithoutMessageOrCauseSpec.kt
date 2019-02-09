package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class ThrowingExceptionsWithoutMessageOrCauseSpec : SubjectSpek<ThrowingExceptionsWithoutMessageOrCause>({
    subject {
        ThrowingExceptionsWithoutMessageOrCause(
                TestConfig(mapOf(ThrowingExceptionsWithoutMessageOrCause.EXCEPTIONS to "IllegalArgumentException"))
        )
    }

    given("several exception calls") {

        val code = """
				fun x() {
					IllegalArgumentException(IllegalArgumentException())
					IllegalArgumentException("foo")
					throw IllegalArgumentException()
				}"""

        it("reports calls to the default constructor") {
            assertThat(subject.lint(code)).hasSize(2)
        }

        it("does not report calls to the default constructor with empty configuration") {
            val config = TestConfig(mapOf(ThrowingExceptionsWithoutMessageOrCause.EXCEPTIONS to ""))
            val findings = ThrowingExceptionsWithoutMessageOrCause(config).lint(code)
            assertThat(findings).hasSize(0)
        }
    }

    given("a test code which asserts an exception") {

        it("does not report a call to this exception") {
            val code = """
				fun test() {
					assertThatIllegalArgumentException().isThrownBy { }
				}
			"""
            assertThat(subject.lint(code)).isEmpty()
        }
    }
})
