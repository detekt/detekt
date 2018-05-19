package io.gitlab.arturbosch.detekt.rules.style.optional

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.psiUtil.siblings

/**
 * This rule detects multi-line `if` statements which does have braces.
 * Adding braces would improve readability and avoid possible errors.
 *
 * <noncompliant>
 * val i = 1
 * if (i > 0)
 *    println(i)
 * </noncompliant>
 *
 * <compliant>
 * val x = if (condition) 5 else 4
 * </compliant>
 *
 * @author jkaan
 */
class MandatoryBracesIfStatements(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue("MandatoryBracesIfStatements", Severity.Style,
			"Multi-line if statements detected without braces. " +
					"The braces can be added to improve readability and prevent possible errors.",
			Debt.FIVE_MINS)

	override fun visitIfExpression(expression: KtIfExpression) {
		if (isNotBlockExpression(expression) && hasNewLine(expression)) {
			report(CodeSmell(issue, Entity.from(expression), message = ""))
		}
		super.visitIfExpression(expression)
	}

	private fun hasNewLine(expression: KtIfExpression): Boolean =
			expression.rightParenthesis?.siblings(true, false)
					?.filter { it is PsiWhiteSpace }
					?.firstOrNull { (it as PsiWhiteSpace).textContains('\n') } != null

	private fun isNotBlockExpression(expression: KtIfExpression): Boolean =
			expression.then?.node?.elementType != KtNodeTypes.BLOCK
}
