package io.gitlab.arturbosch.detekt.rules.style.optional

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.isOverridden
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * It is not necessary to define a return type of `Unit` on functions. This rule detects and reports instances where
 * the `Unit` return type is specified on functions.
 *
 * <noncompliant>
 * fun foo(): Unit { }
 * fun foo() = Unit
 * </noncompliant>
 *
 * <compliant>
 * fun foo() { }
 * </compliant>
 *
 * @author Artur Bosch
 * @author Marvin Ramin
 * @author schalkms
 */
class OptionalUnit(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(
			javaClass.simpleName,
			Severity.Style,
			"Return type of 'Unit' is unnecessary and can be safely removed.",
			Debt.FIVE_MINS)

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (function.funKeyword == null) return
		if (function.hasDeclaredReturnType() && function.colon != null) {
			checkFunctionWithExplicitReturnType(function)
		} else if (!function.isOverridden()) {
			checkFunctionWithInferredReturnType(function)
		}
		super.visitNamedFunction(function)
	}

	private fun checkFunctionWithExplicitReturnType(function: KtNamedFunction) {
		val typeReference = function.typeReference
		typeReference?.typeElement?.text?.let {
			if (it == "Unit") {
				report(CodeSmell(issue, Entity.from(typeReference), createMessage(function)))
			}
		}
	}

	private fun checkFunctionWithInferredReturnType(function: KtNamedFunction) {
		val referenceExpression = function.bodyExpression as? KtNameReferenceExpression
		if (referenceExpression != null && referenceExpression.text == "Unit") {
			report(CodeSmell(issue, Entity.from(referenceExpression), createMessage(function)))
		}
	}

	private fun createMessage(function: KtNamedFunction) = "The function ${function.name} " +
			"defines a return type of Unit. This is unnecessary and can safely be removed."
}
