package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class UnreachableCodeSpec : SubjectSpek<UnreachableCode>({
	subject { UnreachableCode(Config.empty) }

	given("several unreachable statements") {

		it("reports unreachable code") {
			val path = Case.UnreachableCode.path()
			assertThat(subject.lint(path)).hasSize(6)
		}
	}
})
