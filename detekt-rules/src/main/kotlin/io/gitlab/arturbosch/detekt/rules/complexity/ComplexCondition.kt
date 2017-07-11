package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.ThresholdRule
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import io.gitlab.arturbosch.detekt.rules.collectByType
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtDoWhileExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtWhileExpression

/**
 * @author Artur Bosch
 */
class ComplexCondition(config: Config = Config.empty, threshold: Int = 3) : ThresholdRule(config, threshold) {

	override val issue = Issue("ComplexCondition",
			Severity.Maintainability,
			"Complex conditions should be simplified and extracted " +
					"into well-named methods if necessary.")

	override fun visitIfExpression(expression: KtIfExpression) {
		val condition = expression.condition
		checkIfComplex(condition)
		super.visitIfExpression(expression)
	}

	override fun visitDoWhileExpression(expression: KtDoWhileExpression) {
		val condition = expression.condition
		checkIfComplex(condition)
		super.visitDoWhileExpression(expression)
	}

	override fun visitWhileExpression(expression: KtWhileExpression) {
		val condition = expression.condition
		checkIfComplex(condition)
		super.visitWhileExpression(expression)
	}

	private fun checkIfComplex(condition: KtExpression?) {
		val binaryExpressions = condition?.collectByType<KtBinaryExpression>()

		if (binaryExpressions != null && binaryExpressions.size > 1) {
			val longestBinExpr = binaryExpressions.reduce { acc, binExpr ->
				if (binExpr.text.length > acc.text.length) binExpr else acc
			}
			val conditionString = longestBinExpr.text
			val count = frequency(conditionString, "&&") + frequency(conditionString, "||") + 1
			if (count > threshold) {
				report(ThresholdedCodeSmell(issue, Entity.from(condition), Metric("SIZE", count, threshold)))
			}
		}
	}

	private fun frequency(source: String, part: String): Int {

		if (source.isEmpty() || part.isEmpty()) {
			return 0
		}

		var count = 0
		var pos = source.indexOf(part, 0)
		while (pos != -1) {
			pos += part.length
			count++
			pos = source.indexOf(part, pos)
		}

		return count
	}
}
