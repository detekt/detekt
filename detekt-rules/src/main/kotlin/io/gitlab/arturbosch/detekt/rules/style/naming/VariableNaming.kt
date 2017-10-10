package io.gitlab.arturbosch.detekt.rules.style.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.SubRule
import org.jetbrains.kotlin.psi.KtVariableDeclaration
import org.jetbrains.kotlin.resolve.calls.util.isSingleUnderscore

class VariableNaming(config: Config = Config.empty) : SubRule<KtVariableDeclaration>(config) {
	override val issue = Issue(javaClass.simpleName,
			Severity.Style,
			debt = Debt.FIVE_MINS)
	private val variablePattern = Regex(valueOrDefault(VARIABLE_PATTERN, "^(_)?[a-z$][a-zA-Z$0-9]*$"))

	override fun apply(element: KtVariableDeclaration) {
		if (element.isSingleUnderscore) {
			return
		}

		if (!element.identifierName().matches(variablePattern)) {
			report(CodeSmell(
					issue.copy(description = "Variable names should match the pattern: $variablePattern"),
					Entity.from(element)))
		}
	}

	fun doesntMatchPattern(element: KtVariableDeclaration) = !element.identifierName().matches(variablePattern)

	companion object {
		const val VARIABLE_PATTERN = "variablePattern"
	}
}
