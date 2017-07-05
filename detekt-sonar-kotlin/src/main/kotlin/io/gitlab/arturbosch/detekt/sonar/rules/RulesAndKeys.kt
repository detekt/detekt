package io.gitlab.arturbosch.detekt.sonar.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.sonar.foundation.DETEKT_REPOSITORY
import org.sonar.api.rule.RuleKey
import org.sonar.api.rule.RuleStatus
import org.sonar.api.server.rule.RulesDefinition
import java.util.ServiceLoader
import org.sonar.api.rule.Severity as SonarSeverity

private val CONFIG = Config.empty

val ALL_LOADED_RULES = ServiceLoader.load(RuleSetProvider::class.java,
		Config::javaClass.javaClass.classLoader)
		.asIterable()
		.flatMap { it.instance(CONFIG).rules }

val RULE_KEYS = ALL_LOADED_RULES.map { defineRuleKey(it.id) }
private fun defineRuleKey(id: String): RuleKey = RuleKey.of(DETEKT_REPOSITORY, id)

fun findKey(id: String) = RULE_KEYS.find { it.rule() == id }

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
