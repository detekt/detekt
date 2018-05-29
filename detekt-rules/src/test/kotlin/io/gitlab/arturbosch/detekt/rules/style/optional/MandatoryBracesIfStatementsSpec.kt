package io.gitlab.arturbosch.detekt.rules.style.optional

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class MandatoryBracesIfStatementsSpec : SubjectSpek<MandatoryBracesIfStatements>({
	subject { MandatoryBracesIfStatements(Config.empty) }

	given("if statements") {

		it("reports multi-line if statements should have braces") {
			val path = Case.MandatoryBracesIfStatementsPositive.path()
			assertThat(subject.lint(path)).hasSize(7)
		}

		it("reports non multi-line if statements should have braces") {
			val path = Case.MandatoryBracesIfStatementsNegative.path()
			assertThat(subject.lint(path)).hasSize(0)
		}
	}
})
