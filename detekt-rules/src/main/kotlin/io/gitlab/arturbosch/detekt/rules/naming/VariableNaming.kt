package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.naming.util.isContainingExcludedClass
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.isPrivate
import org.jetbrains.kotlin.resolve.calls.util.isSingleUnderscore

/**
 * Reports when variable names which do not follow the specified naming convention are used.
 *
 * @configuration variablePattern - naming pattern (default: '[a-z][A-Za-z0-9]*')
 * @configuration privateVariablePattern - naming pattern (default: '(_)?[a-z][A-Za-z0-9]*')
 * @configuration excludeClassPattern - ignores variables in classes which match this regex (default: '$^')
 *
 * @active since v1.0.0
 * @author Marvin Ramin
 * @author schalkms
 */
class VariableNaming(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Style,
			"Variable names should follow the naming convention set in the projects configuration.",
			debt = Debt.FIVE_MINS)

	private val variablePattern = Regex(valueOrDefault(VARIABLE_PATTERN, "[a-z][A-Za-z\\d]*"))
	private val privateVariablePattern = Regex(valueOrDefault(PRIVATE_VARIABLE_PATTERN, "(_)?[a-z][A-Za-z\\d]*"))
	private val excludeClassPattern = Regex(valueOrDefault(EXCLUDE_CLASS_PATTERN, "$^"))

	override fun visitProperty(property: KtProperty) {
		if (property.isSingleUnderscore || property.isContainingExcludedClass(excludeClassPattern)) {
			return
		}

		val identifier = property.identifierName()
		if (property.isPrivate()) {
			if (!identifier.matches(privateVariablePattern)) {
				report(CodeSmell(
						issue,
						Entity.from(property),
						message = "Private variable names should match the pattern: $privateVariablePattern"))
			}
		} else {
			if (!identifier.matches(variablePattern)) {
				report(CodeSmell(
						issue,
						Entity.from(property),
						message = "Variable names should match the pattern: $variablePattern"))
			}
		}
	}

	companion object {
		const val VARIABLE_PATTERN = "variablePattern"
		const val PRIVATE_VARIABLE_PATTERN = "privateVariablePattern"
		const val EXCLUDE_CLASS_PATTERN = "excludeClassPattern"
	}
}
