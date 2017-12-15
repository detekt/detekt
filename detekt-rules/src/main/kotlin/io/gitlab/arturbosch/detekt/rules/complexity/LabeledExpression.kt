package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtExpressionWithLabel

/**
 *
 * <noncompliant>
 * val range = listOf<String>("foo", "bar")
 * loop@ for (r in range) {
 *     if (r == "bar") break@loop
 *     println(r)
 * }
 * </noncompliant>
 *
 * <compliant>
 * val range = listOf<String>("foo", "bar")
 * for (r in range) {
 *     if (r == "bar") break
 *     println(r)
 * }
 * </compliant>
 *
 * @author Ivan Balaksha
 */
class LabeledExpression(config: Config = Config.empty) : Rule(config) {
	override val issue: Issue = Issue("LabeledExpression",
			Severity.Maintainability,
			"Expression with labels increase complexity and affect maintainability.")

	override fun visitExpressionWithLabel(expression: KtExpressionWithLabel) {
		super.visitExpressionWithLabel(expression)
		expression.getLabelName()?.let {
			report(CodeSmell(issue, Entity.from(expression), message = ""))
		}
	}
}
