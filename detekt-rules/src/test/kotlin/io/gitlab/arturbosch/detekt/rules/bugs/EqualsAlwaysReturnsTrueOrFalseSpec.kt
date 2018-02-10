package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class EqualsAlwaysReturnsTrueOrFalseSpec : SubjectSpek<EqualsAlwaysReturnsTrueOrFalse>({
	subject { EqualsAlwaysReturnsTrueOrFalse(Config.empty) }

	given("several classes overriding the equals() method") {

		it("reports equals() methods") {
			assertThat(subject.lint(Case.EqualsAlwaysReturnsTrueOrFalsePositive.path())).hasSize(6)
		}

		it("does not report equals() methods") {
			assertThat(subject.lint(Case.EqualsAlwaysReturnsTrueOrFalseNegative.path())).hasSize(0)
		}
	}
})
