package io.gitlab.arturbosch.detekt.rules.style.optional

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class MandatoryBracesIfStatementsSpec : Spek({
    val subject by memoized { MandatoryBracesIfStatements(Config.empty) }

    describe("MandatoryBracesIfStatements rule") {

        it("reports multi-line if statements should have braces") {
            val path = Case.MandatoryBracesIfStatementsPositive.path()
            assertThat(subject.lint(path)).hasSize(7)
        }

        it("reports non multi-line if statements should have braces") {
            val path = Case.MandatoryBracesIfStatementsNegative.path()
            assertThat(subject.lint(path)).isEmpty()
        }
    }
})
