package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.asBlockExpression
import io.gitlab.arturbosch.detekt.rules.bugs.util.isEqualsMethod
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtReturnExpression

class EqualsAlwaysReturnsTrueOrFalse(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue("EqualsAlwaysReturnsTrueOrFalse",
			Severity.Defect,
			"Having an equals method which always returns true or false is not a good idea. " +
					"It does not follow the contract of this method. " +
					"Consider a good default implementation. " +
					"For example this == other")

	override fun visitClass(klass: KtClass) {
		if (!klass.isInterface()) {
			super.visitClass(klass)
		}
	}

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (function.isEqualsMethod() && isReturningBooleanConstant(function)) {
			report(CodeSmell(issue, Entity.from(function)))
		}
	}

	private fun isReturningBooleanConstant(function: KtNamedFunction): Boolean {
		val returnExpression = function.bodyExpression.asBlockExpression()?.statements?.lastOrNull() as? KtReturnExpression
		val text = returnExpression?.returnedExpression?.text
		return text == KtTokens.TRUE_KEYWORD.value || text == KtTokens.FALSE_KEYWORD.value
	}
}
