package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * @configuration functionPattern - naming pattern (default: '^([a-z$][a-zA-Z$0-9]*)|(`.*`)$')
 * @active since v1.0.0
 * @author Marvin Ramin
 */
class FunctionNaming(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Style,
			"Function names should follow the naming convention set in the configuration.",
			debt = Debt.FIVE_MINS)
	private val functionPattern = Regex(valueOrDefault(FUNCTION_PATTERN, "^([a-z$][a-zA-Z$0-9]*)|(`.*`)$"))

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (!function.identifierName().matches(functionPattern)) {
			report(CodeSmell(
					issue,
					Entity.from(function),
					message = "Function names should match the pattern: $functionPattern"))
		}
	}

	companion object {
		const val FUNCTION_PATTERN = "functionPattern"
	}
}
