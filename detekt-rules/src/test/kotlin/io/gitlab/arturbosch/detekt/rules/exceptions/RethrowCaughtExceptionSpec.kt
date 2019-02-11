package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class RethrowCaughtExceptionSpec : Spek({
    val subject by memoized { RethrowCaughtException() }

    describe("RethrowCaughtException rule") {

        it("reports caught exceptions which are rethrown") {
            val path = Case.RethrowCaughtExceptionPositive.path()
            assertThat(subject.lint(path)).hasSize(3)
        }

        it("does not report caught exceptions which are encapsulated in another exception or logged") {
            val path = Case.RethrowCaughtExceptionNegative.path()
            assertThat(subject.lint(path)).hasSize(0)
        }
    }
})
