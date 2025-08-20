package dev.detekt.generator.collection

import dev.detekt.generator.collection.exception.InvalidDocumentationException
import org.jetbrains.kotlin.psi.KtFile

class DetektCollector(textReplacements: Map<String, String>) : Collector<RuleSetPage> {

    private val ruleSetProviderCollector = RuleSetProviderCollector()
    private val ruleCollector = RuleCollector(textReplacements)

    private val collectors = listOf(
        ruleSetProviderCollector,
        ruleCollector
    )
    override val items: List<RuleSetPage>
        get() = buildRuleSetPages()

    private fun buildRuleSetPages(): List<RuleSetPage> {
        val rules = ruleCollector.items
        val ruleSets = ruleSetProviderCollector.items

        return ruleSets.map { ruleSet ->
            val sortedRules = ruleSet.rules
                .map { rules.findRuleByName(it) }
                .sortedBy { rule -> rule.name }

            RuleSetPage(ruleSet, sortedRules)
        }
    }

    private fun List<Rule>.findRuleByName(ruleName: String): Rule =
        find { it.name == ruleName } ?: throw InvalidDocumentationException(
            "Rule '$ruleName' was specified in a provider but it was not defined."
        )

    override fun visit(file: KtFile) {
        collectors.forEach {
            it.visit(file)
        }
    }
}
