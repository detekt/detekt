package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtConstructorDelegationCall
import org.jetbrains.kotlin.psi.KtLambdaArgument
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtParenthesizedExpression
import org.jetbrains.kotlin.psi.KtPsiUtil
import org.jetbrains.kotlin.psi.KtSuperTypeCallEntry
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtValueArgumentList

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
		if (argument.children.any { it is KtLambdaExpression } &&
				isSingleLambdaInArgumentList(argument) &&
				!isArgumentInFunctionCallWithTwoLambdas(argument)) {
			val parent = argument.parent
			val message = "Parentheses around the lambda ${parent.text} are unnecessary and can be removed."
			report(CodeSmell(issue, Entity.from(parent), message))
		}
	}

	private fun isSingleLambdaInArgumentList(argument: KtValueArgument): Boolean {
		val parent = argument.parent
		val isOnlyArgument = parent.children.size == 1
		val nodeBeforeArgumentList = parent.parent
		val isSuperTypeCallEntry = nodeBeforeArgumentList is KtSuperTypeCallEntry ||
				nodeBeforeArgumentList is KtConstructorDelegationCall

		return isOnlyArgument &&
				!isSuperTypeCallEntry &&
				argument.equalsToken == null
	}

	private fun isArgumentInFunctionCallWithTwoLambdas(argument: KtValueArgument): Boolean {
		val parent = argument.parent
		val grandParent = parent.parent

		return parent is KtValueArgumentList &&
				grandParent.children.size >= 2 &&
				grandParent.children.last() is KtLambdaArgument
	}
}
