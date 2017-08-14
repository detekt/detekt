package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.RuleTest
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.junit.jupiter.api.Test

/**
 * @author Karol Wrótniak
 */
class CommentInsideBlockTest : RuleTest {
	override val rule = NotConfiguredEmptyRule()

	@Test
	fun findsCommentInsideBlock() {
		val findings = rule.lint("{/*comment*/}")
		Assertions.assertThat(findings).isEmpty()
	}

	@Test
	fun doesNotFindCommentInsideEmptyBlock() {
		val findings = rule.lint("{}")
		Assertions.assertThat(findings).hasSize(1)
	}

	class NotConfiguredEmptyRule : EmptyRule(Config.empty) {
		override fun visitBlockExpression(expression: KtBlockExpression) = expression.addFindingIfBlockExprIsEmpty()
	}
}
