package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtParenthesizedExpression
import org.jetbrains.kotlin.psi.KtPsiUtil
import org.jetbrains.kotlin.psi.KtSuperTypeCallEntry
import org.jetbrains.kotlin.psi.KtValueArgument

/**
 * This rule reports unnecessary parentheses around expressions.
 * These unnecessary parentheses can safely be removed.
 *
 * Added in v1.0.0.RC4
 *
 * <noncompliant>
 * val local = (5 + 3)
 *
 * if ((local == 8)) { }
 *
 * fun foo() {
 *     function({ input -> println(input) })
 * }
 * </noncompliant>
 *
 * <compliant>
 * val local = 5 + 3
 *
 * if (local == 8) { }
 *
 * fun foo() {
 *     function { input -> println(input) }
 * }
 * </compliant>
 *
 * @author Marvin Ramin
 * @author schalkms
 */
class UnnecessaryParentheses(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue("UnnecessaryParentheses", Severity.Style,
			"Unnecessary parentheses don't add any value to the code and should be removed.",
			Debt.FIVE_MINS)

	override fun visitParenthesizedExpression(expression: KtParenthesizedExpression) {
		super.visitParenthesizedExpression(expression)

		if (KtPsiUtil.areParenthesesUseless(expression)) {
			val message = "Parentheses in ${expression.text} are unnecessary and can be replaced with: " +
					"${KtPsiUtil.deparenthesize(expression)?.text}"
			report(CodeSmell(issue, Entity.from(expression), message))
		}
	}

	override fun visitArgument(argument: KtValueArgument) {
		super.visitArgument(argument)
		if (argument.children.any { it is KtLambdaExpression }) {
			val parent = argument.parent
			val isOnlyArgument = parent.children.size == 1
			val isSuperTypeCallEntry = parent.parent is KtSuperTypeCallEntry
			if (isOnlyArgument && !isSuperTypeCallEntry && argument.equalsToken == null) {
				val message = "Parentheses around the lambda ${parent.text} are unnecessary and can be removed."
				report(CodeSmell(issue, Entity.from(parent), message))
			}
		}
	}
}
