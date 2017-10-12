package io.gitlab.arturbosch.detekt.rules.style.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtNamedFunction

class FunctionNaming(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Style,
			debt = Debt.FIVE_MINS)
	private val functionPattern = Regex(valueOrDefault(FUNCTION_PATTERN, "^([a-z$][a-zA-Z$0-9]*)|(`.*`)$"))

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (!function.identifierName().matches(functionPattern)) {
			report(CodeSmell(
					issue.copy(description = "Function names should match the pattern: $functionPattern"),
					Entity.from(function)))
		}
	}

	companion object {
		const val FUNCTION_PATTERN = "functionPattern"
	}
}
