package io.gitlab.arturbosch.detekt.generator.collection

import io.gitlab.arturbosch.detekt.generator.printer.rulesetpage.RuleSetPage
import org.jetbrains.kotlin.psi.KtFile

/**
 * @author Marvin Ramin
 */
class DetektCollector : Collector<RuleSetPage> {

	private val ruleSetProviderCollector = RuleSetProviderCollector()
	private val ruleCollector = RuleCollector()
	private val multiRuleCollector = MultiRuleCollector()

	private val collectors = listOf(
			ruleSetProviderCollector,
			multiRuleCollector,
			ruleCollector
	)
	override val items: List<RuleSetPage>
		get() = buildRuleSetPages()

	private fun buildRuleSetPages(): List<RuleSetPage> {
		val rules = ruleCollector.items
		val multiRules = multiRuleCollector.items.associateBy({ it.name }, { it.rules })
		val ruleSets = ruleSetProviderCollector.items

		return ruleSets.map { ruleSet ->
			val consolidatedRules = ruleSet.rules.flatMap { ruleName ->
				multiRules[ruleName] ?: listOf(ruleName)
			}.map { rules.findRuleByName(it) }
					.sortedBy { rule -> rule.name }

			consolidatedRules.resolveParentRule(rules)
			RuleSetPage(ruleSet, consolidatedRules)
		}
	}

	private fun List<Rule>.findRuleByName(ruleName: String): Rule {
		val rule = this.find { it.name == ruleName }
		if (rule == null) {
			throw InvalidDocumentationException("Rule $ruleName was specified in a provider but it was not defined.")
		}
		return rule
	}

	private fun List<Rule>.resolveParentRule(rules: List<Rule>) {
		this
				.filter { it.debt.isEmpty() && it.severity.isEmpty() }
				.forEach {
					val parentRule = rules.findRuleByName(it.parent)
					it.debt = parentRule.debt
					it.severity = parentRule.severity
				}
	}

	override fun visit(file: KtFile) {
		collectors.forEach {
			it.visit(file)
		}
	}
}
