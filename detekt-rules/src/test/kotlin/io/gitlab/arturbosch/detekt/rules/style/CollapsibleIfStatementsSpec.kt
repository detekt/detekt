package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class CollapsibleIfStatementsSpec : Spek({
    val subject by memoized { CollapsibleIfStatements(Config.empty) }

    describe("CollapsibleIfStatements rule") {

        it("reports if statements which can be merged") {
            val path = Case.CollapsibleIfsPositive.path()
            assertThat(subject.lint(path)).hasSize(2)
        }

        it("does not report if statements which can't be merged") {
            val path = Case.CollapsibleIfsNegative.path()
            assertThat(subject.lint(path)).isEmpty()
        }
    }
})
