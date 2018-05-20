package io.gitlab.arturbosch.detekt.rules.style.optional

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class PreferToOverPairSyntaxSpec : SubjectSpek<PreferToOverPairSyntax>({
	subject { PreferToOverPairSyntax(Config.empty) }

	given("pair objects") {

		it("reports if pair is created using pair constructor") {
			val path = Case.PreferToOverPairSyntaxPositive.path()
			Assertions.assertThat(subject.lint(path)).hasSize(5)
		}

		it("does not report if it is created using the to syntax ") {
			val path = Case.PreferToOverPairSyntaxNegative.path()
			Assertions.assertThat(subject.lint(path)).hasSize(0)
		}
	}
})
