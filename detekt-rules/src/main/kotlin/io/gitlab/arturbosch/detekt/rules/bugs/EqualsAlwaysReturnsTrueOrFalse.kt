package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.asBlockExpression
import io.gitlab.arturbosch.detekt.rules.collectByType
import io.gitlab.arturbosch.detekt.rules.isEqualsFunction
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtReturnExpression

/**
 * Reports equals() methods which will always return true or false.
 *
 * Equals methods should always report if some other object is equal to the current object.
 * See the Kotlin documentation for Any.equals(other: Any?):
 * https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html
 *
 * <noncompliant>
 * override fun equals(other: Any?): Boolean {
 *     return true
 * }
 * </noncompliant>
 *
 * <compliant>
 * override fun equals(other: Any?): Boolean {
 *     return this == other
 * }
 * </compliant>
 *
 * @author schalkms
 * @author Marvin Ramin
 */
class EqualsAlwaysReturnsTrueOrFalse(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue("EqualsAlwaysReturnsTrueOrFalse",
			Severity.Defect,
			"Having an equals method which always returns true or false is not a good idea. " +
					"It does not follow the contract of this method. " +
					"Consider a good default implementation. " +
					"For example this == other",
			Debt.TWENTY_MINS)

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (function.isEqualsFunction() && isReturningBooleanConstant(function)) {
			report(CodeSmell(issue, Entity.from(function), "This equals function always returns the same " +
					"result regardless on the input parameters."))
		}
	}

	private fun isReturningBooleanConstant(function: KtNamedFunction): Boolean {
		val bodyExpression = function.bodyExpression ?: return false
		return if (bodyExpression is KtConstantExpression) {
			bodyExpression.isBooleanConstant()
		} else {
			isSingleReturnWithBooleanConstant(bodyExpression)
		}
	}

	private fun isSingleReturnWithBooleanConstant(bodyExpression: KtExpression): Boolean {
		val returnExpressionsInBlock = bodyExpression.asBlockExpression()?.statements
				?.filterIsInstance<KtReturnExpression>() ?: return false
		val lastValidReturnExpression = returnExpressionsInBlock.first().returnedExpression
		val allReturnExpressions = bodyExpression.collectByType<KtReturnExpression>()
		val hasNoNestedReturnExpression = allReturnExpressions.size == returnExpressionsInBlock.size
		return lastValidReturnExpression?.isBooleanConstant() == true
				&& (hasNoNestedReturnExpression
					|| allReturnExpressions.all { it.returnedExpression?.text == lastValidReturnExpression.text })
	}

	private fun PsiElement.isBooleanConstant() = node.elementType == KtNodeTypes.BOOLEAN_CONSTANT
}
