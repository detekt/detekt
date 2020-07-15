package io.gitlab.arturbosch.detekt.rules.bugs

import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class IteratorHasNextCallsNextMethodSpec : Spek({
    val subject by memoized { IteratorHasNextCallsNextMethod() }

    describe("IteratorHasNextCallsNextMethod rule") {

        it("reports wrong iterator implementation") {
            val path = resourceAsPath("IteratorImplPositive.kt")
            assertThat(subject.lint(path)).hasSize(4)
        }

        it("does not report correct iterator implementations") {
            val path = resourceAsPath("IteratorImplNegative.kt")
            assertThat(subject.lint(path)).isEmpty()
        }
    }
})
