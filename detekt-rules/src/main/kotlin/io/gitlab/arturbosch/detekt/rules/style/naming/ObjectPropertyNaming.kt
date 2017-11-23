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

class ObjectPropertyNaming(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Style,
			"Constants inside objects should follow the naming convention set in the projects configuration.",
			debt = Debt.FIVE_MINS)

	private val propertyPattern = Regex(valueOrDefault(PROPERTY_PATTERN, "[A-Za-z][_A-Za-z\\d]*"))

	override fun visitProperty(property: KtProperty) {
		if (doesntMatchPattern((property))) {
			report(CodeSmell(
					issue,
					Entity.from(property),
					message = "Names of constants inside objects should match the pattern: $propertyPattern"))
		}
	}

	fun doesntMatchPattern(element: KtVariableDeclaration) = !element.identifierName().matches(propertyPattern)

	companion object {
		const val PROPERTY_PATTERN = "propertyPattern"
	}
}
