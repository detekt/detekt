package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class NestedClassesVisibilitySpec : Spek({
    val subject by memoized { NestedClassesVisibility() }

    describe("NestedClassesVisibility rule") {

        it("reports public nested classes") {
            assertThat(subject.lint(Case.NestedClassVisibilityPositive.path())).hasSize(6)
        }

        it("does not report internal and (package) private nested classes") {
            assertThat(subject.lint(Case.NestedClassVisibilityNegative.path())).isEmpty()
        }
    }
})
