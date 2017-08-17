package io.gitlab.arturbosch.detekt.rules.style.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.SubRule
import org.jetbrains.kotlin.psi.KtVariableDeclaration

class VariableMaxLength(config: Config = Config.empty) : SubRule<KtVariableDeclaration>(config) {
	override val issue = Issue(javaClass.simpleName,
			Severity.Style,
			debt = Debt.FIVE_MINS)
	private val maximumVariableNameLength
			= valueOrDefault(MAXIMUM_VARIABLE_NAME_LENGTH, DEFAULT_MAXIMUM_VARIABLE_NAME_LENGTH)

	override fun apply(element: KtVariableDeclaration) {
		if (element.identifierName().length > maximumVariableNameLength) {
			report(CodeSmell(
					issue.copy(description = "Variable names should be at most $maximumVariableNameLength characters long."),
					Entity.from(element)))
		}
	}

	companion object {
		const val MAXIMUM_VARIABLE_NAME_LENGTH = "minimumVariableNameLength"
		private const val DEFAULT_MAXIMUM_VARIABLE_NAME_LENGTH = 30
	}
}
