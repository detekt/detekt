package io.gitlab.arturbosch.detekt.rules.style.optional

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class PreferToOverPairSyntaxSpec : Spek({
    val subject by memoized { PreferToOverPairSyntax(Config.empty) }

    describe("PreferToOverPairSyntax rule") {

        it("reports if pair is created using pair constructor") {
            val path = Case.PreferToOverPairSyntaxPositive.path()
            assertThat(subject.lint(path)).hasSize(5)
        }

        it("does not report if it is created using the to syntax ") {
            val path = Case.PreferToOverPairSyntaxNegative.path()
            assertThat(subject.lint(path)).hasSize(0)
        }
    }
})
