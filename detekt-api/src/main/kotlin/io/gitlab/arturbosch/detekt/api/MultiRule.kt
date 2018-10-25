package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.psi.KtFile

abstract class MultiRule : BaseRule() {

	abstract val rules: List<Rule>
	var activeRules: Set<Rule> by SingleAssign()
	var ruleFilters: Set<RuleId> = emptySet()

	override fun visitCondition(root: KtFile) = true

	override fun preVisit(root: KtFile) {
		activeRules = rules.filterTo(HashSet()) {
			it.ruleId !in ruleFilters && it.visitCondition(root)
		}
	}

	override fun postVisit(root: KtFile) {
		for (activeRule in activeRules) {
			// TODO do not actually need a suppression again
			report(activeRule.findings, activeRule.aliases)
		}
	}

	fun <T : Rule> T.runIfActive(block: T.() -> Unit) {
		if (this in activeRules) {
			block()
		}
	}
}
