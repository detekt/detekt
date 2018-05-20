package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.getIntValueForPsiElement
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtParenthesizedExpression
import org.jetbrains.kotlin.psi.psiUtil.getCallNameExpression
import org.jetbrains.kotlin.psi.psiUtil.getReceiverExpression

/**
 * Using the forEach method on ranges has a heavy performance cost. Prefer using simple for loops.
 *
 * Benchmarks have shown that using forEach on a range can have a huge performance cost in comparison to
 * simple for loops. Hence in most contexts a simple for loop should be used instead.
 * See more details here: https://sites.google.com/a/athaydes.com/renato-athaydes/posts/kotlinshiddencosts-benchmarks
 * To solve this CodeSmell, the forEach usage should be replaced by a for loop.
 *
 * <noncompliant>
 * (1..10).forEach {
 *     println(it)
 * }
 * (1 until 10).forEach {
 *     println(it)
 * }
 * (10 downTo 1).forEach {
 *     println(it)
 * }
 * </noncompliant>
 *
 * <compliant>
 * for (i in 1..10) {
 *     println(i)
 * }
 * </compliant>
 *
 * @active since v1.0.0
 *
 * @author Marvin Ramin
 * @author Ivan Balaksha
 * @author schalkms
 */
class ForEachOnRange(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue("ForEachOnRange",
			Severity.Performance,
			"Using the forEach method on ranges has a heavy performance cost. Prefer using simple for loops.",
			Debt.FIVE_MINS)

	private val minimumRangeSize = 3
	private val rangeOperators = setOf("..", "downTo", "until")

	override fun visitCallExpression(expression: KtCallExpression) {
		super.visitCallExpression(expression)

		expression.getCallNameExpression()?.let {
			if (!it.textMatches("forEach")) {
				return
			}
			val parenthesizedExpression = it.getReceiverExpression() as? KtParenthesizedExpression
			val binaryExpression = parenthesizedExpression?.expression as? KtBinaryExpression
			if (binaryExpression != null && isRangeOperator(binaryExpression)) {
				report(CodeSmell(issue, Entity.from(expression), issue.description))
			}
		}
	}

	private fun isRangeOperator(binaryExpression: KtBinaryExpression): Boolean {
		val range = binaryExpression.children
		if (range.size >= minimumRangeSize) {
			val lowerValue = getIntValueForPsiElement(range[0])
			val upperValue = getIntValueForPsiElement(range[2])
			return lowerValue != null && upperValue != null && rangeOperators.contains(range[1].text)
		}
		return false
	}
}
