package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.*
import io.gitlab.arturbosch.detekt.rules.collectByType
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtDoWhileExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtWhileExpression

/**
 * @author Artur Bosch
 */
class ComplexCondition(config: Config = Config.empty, threshold: Int = 3) :
		ThresholdRule("ComplexCondition", config, threshold) {

	override fun visitIfExpression(context: Context, expression: KtIfExpression) {
		val condition = expression.condition
		checkIfComplex(context, condition)
		super.visitIfExpression(context, expression)
	}

	override fun visitDoWhileExpression(context: Context, expression: KtDoWhileExpression) {
		val condition = expression.condition
		checkIfComplex(context, condition)
		super.visitDoWhileExpression(context, expression)
	}

	override fun visitWhileExpression(context: Context, expression: KtWhileExpression) {
		val condition = expression.condition
		checkIfComplex(context, condition)
		super.visitWhileExpression(context, expression)
	}

	private fun checkIfComplex(context: Context, condition: KtExpression?) {
		val binaryExpressions = condition?.collectByType<KtBinaryExpression>(context)

		if (binaryExpressions != null && binaryExpressions.size > 1) {
			val longestBinExpr = binaryExpressions.reduce { acc, binExpr ->
				if (binExpr.text.length > acc.text.length) binExpr else acc
			}
			val conditionString = longestBinExpr.text
			val count = frequency(conditionString, "&&") + frequency(conditionString, "||") + 1
			if (count > threshold) {
				context.report(ThresholdedCodeSmell(ISSUE, Entity.from(condition), Metric("SIZE", count, threshold)))
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

	companion object {
		val ISSUE = Issue("ComplexCondition", Issue.Severity.CodeSmell)
	}
}