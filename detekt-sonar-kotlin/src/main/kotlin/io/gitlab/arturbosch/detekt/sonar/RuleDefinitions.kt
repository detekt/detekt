package io.gitlab.arturbosch.detekt.sonar

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.formatting.FormattingProvider
import io.gitlab.arturbosch.detekt.rules.complexity.ComplexityProvider
import io.gitlab.arturbosch.detekt.rules.documentation.CommentSmellProvider
import io.gitlab.arturbosch.detekt.rules.providers.CodeSmellProvider
import io.gitlab.arturbosch.detekt.rules.providers.EmptyCodeProvider
import io.gitlab.arturbosch.detekt.rules.providers.ExceptionsProvider
import io.gitlab.arturbosch.detekt.rules.providers.PotentialBugProvider
import io.gitlab.arturbosch.detekt.rules.style.StyleGuideProvider
import org.sonar.api.rule.RuleKey
import org.sonar.api.rule.RuleStatus
import org.sonar.api.rule.Severity as SonarSeverity
import org.sonar.api.server.rule.RulesDefinition

private val CONFIG = Config.empty

private val COMPLEXITY_RULES = ComplexityProvider().instance(CONFIG).rules
private val STYLES_RULES = StyleGuideProvider().instance(CONFIG).rules
private val CODE_SMELL_RULES = CodeSmellProvider().instance(CONFIG).rules
private val COMMENTS_RULES = CommentSmellProvider().instance(CONFIG).rules
private val EMPTY_RULES = EmptyCodeProvider().instance(CONFIG).rules
private val EXCEPTIONS_RULES = ExceptionsProvider().instance(CONFIG).rules
private val POTENTIAL_BUGS_RULES = PotentialBugProvider().instance(CONFIG).rules
private val FORMATTING_RULES = FormattingProvider().instance(CONFIG).rules

val RULE_KEYS = COMPLEXITY_RULES.map { defineRuleKey(it.id) } +
		STYLES_RULES.map { defineRuleKey(it.id) } +
		CODE_SMELL_RULES.map { defineRuleKey(it.id) } +
		COMMENTS_RULES.map { defineRuleKey(it.id) } +
		EMPTY_RULES.map { defineRuleKey(it.id) } +
		EXCEPTIONS_RULES.map { defineRuleKey(it.id) } +
		POTENTIAL_BUGS_RULES.map { defineRuleKey(it.id) } +
		FORMATTING_RULES.map { defineRuleKey(it.id) }

fun findKey(id: String) = RULE_KEYS.find { it.rule() == id }

private fun defineRuleKey(id: String): RuleKey = RuleKey.of(DETEKT_REPOSITORY, id)

fun RulesDefinition.NewRepository.createRules() = this.apply {
	COMPLEXITY_RULES.map { defineRule(it, "20min") } +
			STYLES_RULES.map { defineRule(it, "5min") } +
			CODE_SMELL_RULES.map { defineRule(it, "20min") } +
			COMMENTS_RULES.map { defineRule(it, "10min") } +
			EXCEPTIONS_RULES.map { defineRule(it, "10min") } +
			EMPTY_RULES.map { defineRule(it, "5min") } +
			POTENTIAL_BUGS_RULES.map { defineRule(it, "10min") } +
			FORMATTING_RULES.map { defineRule(it, "1min") }
}

private fun RulesDefinition.NewRepository.defineRule(rule: Rule, dept: String) {
	val newRule = createRule(rule.id).setName(rule.id)
			.setHtmlDescription("No description yet for ${rule.id}!")
			.setTags(rule.issue.severity.name.toLowerCase())
			.setStatus(RuleStatus.READY)
			.setSeverity(severityMap[rule.issue.severity])
	newRule.setDebtRemediationFunction(newRule.debtRemediationFunctions().linear(dept))
}

private val severityMap = mapOf(
		Severity.CodeSmell to SonarSeverity.MAJOR,
		Severity.Defect to SonarSeverity.CRITICAL,
		Severity.Maintainability to SonarSeverity.MAJOR,
		Severity.Minor to SonarSeverity.MINOR,
		Severity.Security to SonarSeverity.BLOCKER,
		Severity.Style to SonarSeverity.INFO,
		Severity.Warning to SonarSeverity.INFO
)
