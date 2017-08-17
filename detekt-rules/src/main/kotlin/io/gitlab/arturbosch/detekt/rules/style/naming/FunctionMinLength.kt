package io.gitlab.arturbosch.detekt.rules.style.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.SubRule
import org.jetbrains.kotlin.psi.KtNamedFunction

class FunctionMinLength(config: Config = Config.empty) : SubRule<KtNamedFunction>(config) {
	override val issue = Issue(javaClass.simpleName,
			Severity.Style,
			debt = Debt.FIVE_MINS)
	private val minimumFunctionNameLength
			= valueOrDefault(MINIMUM_FUNCTION_NAME_LENGTH, DEFAULT_MINIMUM_FUNCTION_NAME_LENGTH)

	override fun apply(element: KtNamedFunction) {
		if (element.identifierName().length < minimumFunctionNameLength) {
			report(CodeSmell(
					issue.copy(description = "Function names should be at least $minimumFunctionNameLength characters long."),
					Entity.from(element)))
		}
	}

	companion object {
		const val MINIMUM_FUNCTION_NAME_LENGTH = "minimumFunctionNameLength"
		private const val DEFAULT_MINIMUM_FUNCTION_NAME_LENGTH = 3
	}
}
