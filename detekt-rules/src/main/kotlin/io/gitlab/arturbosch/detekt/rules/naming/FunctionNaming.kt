package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.naming.util.isContainingExcludedClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import java.net.URL

/**
 * Reports when function names which do not follow the specified naming convention are used.
 *
 * @configuration functionPattern - naming pattern (default: '^([a-z$][a-zA-Z$0-9]*)|(`.*`)$')
 * @configuration excludeClassPattern - ignores functions in classes which match this regex (default: '$^')
 *
 * @active since v1.0.0
 * @author Marvin Ramin
 * @author schalkms
 */
class FunctionNaming(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Style,
			"Function names should follow the naming convention set in the configuration.",
			debt = Debt.FIVE_MINS)

	private val functionPattern = Regex(valueOrDefault(FUNCTION_PATTERN, "^([a-z$][a-zA-Z$0-9]*)|(`.*`)$"))
	private val excludeClassPattern = Regex(valueOrDefault(EXCLUDE_CLASS_PATTERN, "$^"))

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (!function.isContainingExcludedClass(excludeClassPattern)
				&& !function.identifierName().matches(functionPattern)) {
			report(CodeSmell(
					issue,
					Entity.from(function),
					message = "Function names should match the pattern: $functionPattern"))
		}
	}

	companion object {
		const val FUNCTION_PATTERN = "functionPattern"
		const val EXCLUDE_CLASS_PATTERN = "excludeClassPattern"
	}
}
