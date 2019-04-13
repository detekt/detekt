package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class DataClassShouldBeImmutableSpec : Spek({
    val subject by memoized { DataClassShouldBeImmutable() }

    describe("DataClassShouldBeImmutable rule") {

        it("reports positive cases") {
            val path = Case.DataClassShouldBeImmutablePositive.path()
            Assertions.assertThat(subject.lint(path)).hasSize(4)
        }

        it("does not report negative cases") {
            val path = Case.DataClassShouldBeImmutableNegative.path()
            Assertions.assertThat(subject.lint(path)).hasSize(0)
        }
    }
})
