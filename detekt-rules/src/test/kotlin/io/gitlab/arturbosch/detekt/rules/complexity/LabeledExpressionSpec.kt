package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

/**
 * @author Ivan Balaksha
 */
class LabeledExpressionSpec : SubjectSpek<LabeledExpression>({

	subject { LabeledExpression() }

	given("several labeled expressions") {

		it("reports these labels") {
			subject.lint(Case.LabeledExpression.path())
			assertThat(subject.findings).hasSize(7)
		}
	}
})
