package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.Finding

inline fun <T> T.applyRules(rules: () -> List<SubRule<T>>): List<Finding> {
	val findings: ArrayList<Finding> = arrayListOf()
	rules.invoke().forEach {
		it.apply(this)
		findings.addAll(it.findings)
	}
	return findings
}

inline fun <T> T.applyRule(rule: () -> SubRule<T>): List<Finding> {
	return this.applyRules { listOf(rule.invoke()) }
}