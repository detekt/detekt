package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class IteratorNotThrowingNoSuchElementExceptionSpec : Spek({
    val subject by memoized { IteratorNotThrowingNoSuchElementException() }

    describe("IteratorNotThrowingNoSuchElementException rule") {

        it("reports invalid next() implementations") {
            val path = Case.IteratorImplPositive.path()
            assertThat(subject.lint(path)).hasSize(4)
        }

        it("does not report correct next() implemenations") {
            val path = Case.IteratorImplNegative.path()
            assertThat(subject.lint(path)).hasSize(0)
        }
    }
})
