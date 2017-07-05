package io.gitlab.arturbosch.detekt.sonar.rules

import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.sonar.foundation.DETEKT_ANALYZER
import io.gitlab.arturbosch.detekt.sonar.foundation.DETEKT_REPOSITORY
import io.gitlab.arturbosch.detekt.sonar.foundation.KOTLIN_KEY
import org.sonar.api.rule.RuleStatus
import org.sonar.api.server.rule.RulesDefinition

/**
 * @author Artur Bosch
 */
class DetektRulesDefinition : RulesDefinition {

	override fun define(context: RulesDefinition.Context) {
		context.createRepository(DETEKT_REPOSITORY, KOTLIN_KEY)
				.setName(DETEKT_ANALYZER)
				.createRules()
				.done()
	}

}

fun RulesDefinition.NewRepository.createRules() = this.apply {
	ALL_LOADED_RULES.map { defineRule(it) }
}

private fun RulesDefinition.NewRepository.defineRule(rule: Rule) {
	var description = rule.issue.description
	// TODO remove this after all rules have descriptions
	if (description.isNullOrBlank()) description = "No description yet!"
	val newRule = createRule(rule.id).setName(rule.id)
			.setHtmlDescription(description)
			.setTags(rule.issue.severity.name.toLowerCase())
			.setStatus(RuleStatus.READY)
			.setSeverity(severityMap[rule.issue.severity])
	newRule.setDebtRemediationFunction(
			newRule.debtRemediationFunctions().linear(rule.issue.dept.toString()))
}

private val severityMap = mapOf(
		Severity.CodeSmell to org.sonar.api.rule.Severity.MAJOR,
		Severity.Defect to org.sonar.api.rule.Severity.CRITICAL,
		Severity.Maintainability to org.sonar.api.rule.Severity.MAJOR,
		Severity.Minor to org.sonar.api.rule.Severity.MINOR,
		Severity.Security to org.sonar.api.rule.Severity.BLOCKER,
		Severity.Style to org.sonar.api.rule.Severity.INFO,
		Severity.Warning to org.sonar.api.rule.Severity.INFO
)