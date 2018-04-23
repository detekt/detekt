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
import org.jetbrains.kotlin.psi.psiUtil.isPrivate

/**
 * Reports when top level constant names which do not follow the specified naming convention are used.
 *
 * @configuration constantPattern - naming pattern (default: '[A-Z][_A-Z0-9]*')
 * @configuration propertyPattern - naming pattern (default: '[A-Za-z][_A-Za-z0-9]*')
 * @configuration privatePropertyPattern - naming pattern (default: '(_)?[A-Za-z][A-Za-z0-9]*')
 *
 * @active since v1.0.0
 * @author Marvin Ramin
 */
class TopLevelPropertyNaming(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Style,
			"Top level constant names should follow the naming convention set in the projects configuration.",
			debt = Debt.FIVE_MINS)

	private val constantPattern = Regex(valueOrDefault(CONSTANT_PATTERN, "[A-Z][_A-Z0-9]*"))
	private val propertyPattern = Regex(valueOrDefault(PROPERTY_PATTERN, "[A-Za-z][_A-Za-z0-9]*"))
	private val privatePropertyPattern = Regex(valueOrDefault(PRIVATE_PROPERTY_PATTERN, "(_)?[A-Za-z][A-Za-z0-9]*"))

	override fun visitProperty(property: KtProperty) {
		if (property.hasConstModifier()) {
			handleConstant(property)
		} else {
			handleProperty(property)
		}
	}

	private fun handleConstant(property: KtProperty) {
		if (!property.identifierName().matches(constantPattern)) {
			report(CodeSmell(
					issue,
					Entity.from(property),
					message = "Top level constant names should match the pattern: $constantPattern"))
		}
	}

	private fun handleProperty(property: KtProperty) {
		if (property.isPrivate()) {
			if (!property.identifierName().matches(privatePropertyPattern)) {
				report(CodeSmell(
						issue,
						Entity.from(property),
						message = "Private top level property names should match the pattern: $propertyPattern"))
			}
		} else {
			if (!property.identifierName().matches(propertyPattern)) {
				report(CodeSmell(
						issue,
						Entity.from(property),
						message = "Top level property names should match the pattern: $propertyPattern"))
			}
		}
	}

	companion object {
		const val CONSTANT_PATTERN = "constantPattern"
		const val PROPERTY_PATTERN = "propertyPattern"
		const val PRIVATE_PROPERTY_PATTERN = "privatePropertyPattern"
	}
}
