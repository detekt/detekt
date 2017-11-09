package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class CollapsibleIfStatementsSpec : SubjectSpek<CollapsibleIfStatements>({
	subject { CollapsibleIfStatements(Config.empty) }

	given("multiple if statements") {

		it("reports if statements which can be merged") {
			val path = Case.CollapsibleIfsPositive.path()
			assertThat(subject.lint(path)).hasSize(2)
		}

		it("does not report if statements which can't be merged") {
			val path = Case.CollapsibleIfsNegative.path()
			assertThat(subject.lint(path)).hasSize(0)
		}
	}
})
