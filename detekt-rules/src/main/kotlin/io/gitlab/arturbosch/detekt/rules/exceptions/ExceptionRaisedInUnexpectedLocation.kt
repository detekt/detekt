package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.collectByType
import io.gitlab.arturbosch.detekt.rules.isPublicAndOverridden
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClassInitializer
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtThrowExpression

class ExceptionRaisedInUnexpectedLocation(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue("ExceptionRaisedInUnexpectedLocation", Severity.CodeSmell,
			"This method is not expected to throw exceptions. This can cause weird behavior.")

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (isPotentialMethod(function) && hasThrowExpression(function.bodyExpression)) {
			report(CodeSmell(issue, Entity.from(function)))
		}
	}

	override fun visitClassInitializer(initializer: KtClassInitializer) {
		if (hasThrowExpression(initializer.body)) {
			report(CodeSmell(issue, Entity.from(initializer)))
		}
	}

	private fun isPotentialMethod(function: KtNamedFunction): Boolean {
		return when (function.name) {
			"toString", "hashCode" -> isToStringOrHashCodeFunction(function)
			"equals" -> isEqualsFunction(function)
			"finalize" -> isFinalizeFunction(function)
			else -> false
		}
	}

	private fun hasThrowExpression(declaration: KtExpression?): Boolean {
		return declaration?.collectByType<KtThrowExpression>()?.any() == true
	}

	private fun isToStringOrHashCodeFunction(function: KtNamedFunction): Boolean {
		return function.isPublicAndOverridden() && function.valueParameters.size == 0
	}

	private fun isEqualsFunction(function: KtNamedFunction): Boolean {
		val parameters = function.valueParameters
		return function.isPublicAndOverridden() && parameters.size == 1 && parameters.first().typeReference?.text == "Any?"
	}

	private fun isFinalizeFunction(function: KtNamedFunction): Boolean {
		return function.hasModifier(KtTokens.PROTECTED_KEYWORD) && function.valueParameters.size == 0
	}
}
