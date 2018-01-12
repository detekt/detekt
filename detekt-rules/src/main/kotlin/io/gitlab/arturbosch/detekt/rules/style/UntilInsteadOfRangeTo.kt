package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtForExpression

/**
 * Reports calls to '..' operator instead of calls to 'until'.
 * 'until' is applicable in cases where upper range value is described as
 * some value subtracted by 1. 'until' helps to prevent off-by-one errors.
 *
 * <noncompliant>
 * for (i in 0 .. 10 - 1) {}
 * </noncompliant>
 * for (i in 0 until 10 - 1) {}
 * for (i in 10 downTo 2 - 1) {}
 * for (i in 0 .. 10) {}
 * for (i in 0 .. 10 + 1) {}
 * for (i in 0 .. 10 - 2) {}
 * <compliant>

 * </compliant>
 *
 * @author Ilya Zorin
 */
class UntilInsteadOfRangeTo(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Style,
			"'..' call can be replaced with 'until'",
			Debt.FIVE_MINS)

	private val minimumSize = 3

	override fun visitForExpression(expression: KtForExpression) {
		val loopRange = expression.loopRange
		val range = loopRange?.children
		if (range != null && range.size >= minimumSize && isUntilApplicable(range)) {
			report(CodeSmell(issue, Entity.from(loopRange),
					"'..' call can be replaced with 'until'"))
		}
		super.visitForExpression(expression)
	}

	private fun isUntilApplicable(range: Array<PsiElement>): Boolean {
		if (range[1].text == "..") {
			val expression = range[2] as? KtBinaryExpression
			if (expression?.operationToken == KtTokens.MINUS) {
				val rightExpressionValue = expression?.right as? KtConstantExpression
				return rightExpressionValue?.text == "1"
			}
		}
		return false
	}
}
