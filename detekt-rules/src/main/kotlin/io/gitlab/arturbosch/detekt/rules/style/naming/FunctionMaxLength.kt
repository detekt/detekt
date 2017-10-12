package io.gitlab.arturbosch.detekt.rules.style.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtNamedFunction

class FunctionMaxLength(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Style,
			debt = Debt.FIVE_MINS)
	private val maximumFunctionNameLength =
			valueOrDefault(MAXIMUM_FUNCTION_NAME_LENGTH, DEFAULT_MAXIMUM_FUNCTION_NAME_LENGTH)

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (function.identifierName().length > maximumFunctionNameLength) {
			report(CodeSmell(
					issue.copy(description = "Function names should be at most $maximumFunctionNameLength characters long."),
					Entity.from(function)))
		}
	}

	companion object {
		const val MAXIMUM_FUNCTION_NAME_LENGTH = "maximumFunctionNameLength"
		private const val DEFAULT_MAXIMUM_FUNCTION_NAME_LENGTH = 30
	}
}
