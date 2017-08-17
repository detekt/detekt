package io.gitlab.arturbosch.detekt.rules.style.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.SubRule
import org.jetbrains.kotlin.psi.KtVariableDeclaration

class ConstantNaming(config: Config = Config.empty) : SubRule<KtVariableDeclaration>(config) {
	override val issue = Issue(javaClass.simpleName,
			Severity.Style,
			debt = Debt.FIVE_MINS)
	private val constantPattern = Regex(valueOrDefault(CONSTANT_PATTERN, "^([A-Z_]*|serialVersionUID)$"))

	override fun apply(element: KtVariableDeclaration) {
		if (doesntMatchPattern((element))) {
			report(CodeSmell(
					issue.copy(description = "Constant names should match the pattern: $constantPattern"),
					Entity.from(element)))
		}
	}

	fun doesntMatchPattern(element: KtVariableDeclaration) = !element.identifierName().matches(constantPattern)

	companion object {
		const val CONSTANT_PATTERN = "constantPattern"
	}
}
