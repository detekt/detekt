package io.gitlab.arturbosch.detekt.rules.style.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtVariableDeclaration
import org.jetbrains.kotlin.resolve.calls.util.isSingleUnderscore

class VariableNaming(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Style,
			"Variable names should follow the naming convention set in the projects configuration.",
			debt = Debt.FIVE_MINS)
	private val variablePattern = Regex(valueOrDefault(VARIABLE_PATTERN, "^(_)?[a-z$][a-zA-Z$0-9]*$"))

	override fun visitProperty(property: KtProperty) {
		if (property.isSingleUnderscore) {
			return
		}

		if (!property.identifierName().matches(variablePattern)) {
			report(CodeSmell(
					issue,
					Entity.from(property),
					message = "Variable names should match the pattern: $variablePattern"))
		}
	}

	fun doesntMatchPattern(element: KtVariableDeclaration) = !element.identifierName().matches(variablePattern)

	companion object {
		const val VARIABLE_PATTERN = "variablePattern"
	}
}
