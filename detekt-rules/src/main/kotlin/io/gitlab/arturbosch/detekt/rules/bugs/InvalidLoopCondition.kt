package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtForExpression

/**
 * Reports loop conditions which will never be triggered.
 * This might be due to invalid ranges like (10..9) which will cause the loop to never be entered.
 *
 * <noncompliant>
 * for (i in 2..1) {}
 * for (i in 1 downTo 2) {}
 * for (i in 2 until 1) {}
 * </noncompliant>
 *
 * <compliant>
 * for (i in 2..2) {}
 * for (i in 2 downTo 2) {}
 * for (i in 2 until 2) {}
 * </compliant>
 *
 * @author schalkms
 * @author Marvin Ramin
 */
class InvalidLoopCondition(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Defect,
			"If a for loops condition is false before the first iteration, the loop will never get executed.",
			Debt.TEN_MINS)

	private val minimumSize = 3

	override fun visitForExpression(expression: KtForExpression) {
		val loopRange = expression.loopRange
		val range = loopRange?.children
		if (range != null && range.size >= minimumSize && hasInvalidLoopRange(range)) {
			report(CodeSmell(issue, Entity.from(loopRange), "This loop will never be executed due to its " +
					"expression."))
		}
		super.visitForExpression(expression)
	}

	private fun hasInvalidLoopRange(range: Array<PsiElement>): Boolean {
		val lowerValue = getIntValueForElement(range[0])
		val upperValue = getIntValueForElement(range[2])
		if (lowerValue == null || upperValue == null) {
			return false
		}
		return when (range[1].text) {
			".." -> checkRangeTo(lowerValue, upperValue)
			"downTo" -> checkDownTo(lowerValue, upperValue)
			"until" -> checkUntil(lowerValue, upperValue)
			else -> false
		}
	}

	private fun getIntValueForElement(element: PsiElement): Int? {
		return (element as? KtConstantExpression)?.text?.toIntOrNull()
	}

	private fun checkRangeTo(lower: Int, upper: Int) = lower > upper

	private fun checkDownTo(lower: Int, upper: Int) = lower < upper

	private fun checkUntil(lower: Int, upper: Int) = lower > upper
}
