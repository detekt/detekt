package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.hasConstModifier
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtVariableDeclaration

/**
 * Reports when property names inside objects which do not follow the specified naming convention are used.
 *
 * @configuration propertyPattern - naming pattern (default: '[A-Za-z][_A-Za-z0-9]*')
 * @configuration constantPattern - naming pattern (default: '[A-Za-z][_A-Za-z0-9]*')
 * @active since v1.0.0
 * @author Marvin Ramin
 * @author schalkms
 */
class ObjectPropertyNaming(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Style,
			"Property names inside objects should follow the naming convention set in the projects configuration.",
			debt = Debt.FIVE_MINS)

	private val propertyPattern = Regex(valueOrDefault(PROPERTY_PATTERN, "[A-Za-z][_A-Za-z\\d]*"))
	private val constantPattern = Regex(valueOrDefault(CONSTANT_PATTERN, "[A-Za-z][_A-Za-z\\d]*"))

	override fun visitProperty(property: KtProperty) {
		if (property.hasConstModifier()) {
			reportIfNotMatching(property, constantPattern)
		} else {
			reportIfNotMatching(property, propertyPattern)
		}
	}

	private fun reportIfNotMatching(property: KtProperty, pattern: Regex) {
		if (doesNotMatchPattern(property, pattern)) {
			report(CodeSmell(
					issue,
					Entity.from(property),
					message = "Names of properties inside objects should match the pattern: $propertyPattern"))
		}
	}

	private fun doesNotMatchPattern(element: KtVariableDeclaration, pattern: Regex) =
			!element.identifierName().matches(pattern)

	companion object {
		const val PROPERTY_PATTERN = "propertyPattern"
		const val CONSTANT_PATTERN = "constantPattern"
	}
}
