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
			val path = Case.CollapsibleIfs.path()
			assertThat(subject.lint(path)).hasSize(2)
		}
	}
})
