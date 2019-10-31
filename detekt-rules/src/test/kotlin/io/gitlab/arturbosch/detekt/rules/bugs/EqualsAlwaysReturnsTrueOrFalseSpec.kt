package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class EqualsAlwaysReturnsTrueOrFalseSpec : Spek({
    val subject by memoized { EqualsAlwaysReturnsTrueOrFalse(Config.empty) }

    describe("Equals Always Returns True Or False rule") {

        it("reports equals() methods") {
            assertThat(subject.lint(Case.EqualsAlwaysReturnsTrueOrFalsePositive.path())).hasSize(6)
        }

        it("does not report equals() methods") {
            assertThat(subject.lint(Case.EqualsAlwaysReturnsTrueOrFalseNegative.path())).isEmpty()
        }

        it("detects and doesn't crash when return expression is annotated - #2021") {
            val code = """
            override fun equals(other: Any?): Boolean {
                @Suppress("UnsafeCallOnNullableType")
                return true
            }
            """
            assertThat(subject.lint(code)).hasSize(1)
        }
    }
})
