package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class IteratorHasNextCallsNextMethodSpec : Spek({
    val subject by memoized { IteratorHasNextCallsNextMethod() }

    describe("IteratorHasNextCallsNextMethod rule") {

        it("reports wrong iterator implementation") {
            val path = Case.IteratorImplPositive.path()
            assertThat(subject.lint(path)).hasSize(4)
        }

        it("does not report correct iterator implementations") {
            val path = Case.IteratorImplNegative.path()
            assertThat(subject.lint(path)).isEmpty()
        }
    }
})
