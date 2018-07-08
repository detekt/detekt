package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

/**
 * @author Karol Wrótniak
 */
class CommentInsideBlockSpec : SubjectSpek<NotConfiguredEmptyRule>({

	subject { NotConfiguredEmptyRule() }

	it("finds comments inside block") {
		val findings = subject.lint("{/*comment*/}")
		assertThat(findings).isEmpty()
	}

	it("does not find comment inside an empty block") {
		val findings = subject.lint("{}")
		assertThat(findings).hasSize(1)
	}
})

class NotConfiguredEmptyRule : EmptyRule(Config.empty) {
	override fun visitBlockExpression(expression: KtBlockExpression) =
			expression.addFindingIfBlockExprIsEmpty()
}
