package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class FunctionOnlyReturningConstantSpec : SubjectSpek<FunctionOnlyReturningConstant>({
	subject { FunctionOnlyReturningConstant() }

	given("some functions which return stuff") {

		it("reports functions which return constants") {
			val path = Case.FunctionReturningConstant.path()
			assertThat(subject.lint(path)).hasSize(5)
		}
	}
})
