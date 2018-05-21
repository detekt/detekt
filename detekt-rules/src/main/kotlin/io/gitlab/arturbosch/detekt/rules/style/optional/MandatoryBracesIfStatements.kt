package io.gitlab.arturbosch.detekt.rules.style.optional

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.psiUtil.siblings

/**
 * This rule detects multi-line `if` statements which do not have braces.
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
			"Multi-line if statement was found that does not have braces. " +
					"These should be added to improve readability.",
			Debt.FIVE_MINS)

	override fun visitIfExpression(expression: KtIfExpression) {
		if (isNotBlockExpression(expression) && hasNewLine(expression)) {
			report(CodeSmell(issue, Entity.from(expression),
					message = "Multi-line if statement was found that does not have braces. " +
							"These should be added to improve readability."))
		}

		if (isNotBlockOrIfExpression(expression) && hasNewLine(expression.elseKeyword)) {
			report(CodeSmell(issue, Entity.from(expression),
					message = "Multi-line else statement was found that does not have braces." +
							"These should be added to improve readability"))
		}

		super.visitIfExpression(expression)
	}

	private fun hasNewLine(expression: KtIfExpression): Boolean =
			expression.rightParenthesis?.siblings(true, false)
					?.filterIsInstance<PsiWhiteSpace>()
					?.firstOrNull { it.textContains('\n') } != null

	private fun hasNewLine(element: PsiElement?): Boolean =
			element?.siblings(true, false)
					?.filterIsInstance<PsiWhiteSpace>()
					?.firstOrNull { it.textContains('\n') } != null

	private fun isNotBlockExpression(expression: KtIfExpression): Boolean =
			expression.then !is KtBlockExpression

	private fun isNotBlockOrIfExpression(expression: KtIfExpression): Boolean =
			expression.`else` != null
					&& expression.`else` !is KtIfExpression
					&& expression.`else` !is KtBlockExpression
}
