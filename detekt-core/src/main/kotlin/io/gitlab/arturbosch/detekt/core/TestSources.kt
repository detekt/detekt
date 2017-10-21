package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.core.TestPattern.Companion.TEST_PATTERN_SUB_CONFIG
import org.jetbrains.kotlin.psi.KtFile

/**
 * @author Artur Bosch
 */

fun createTestPattern(config: Config) = with(config.subConfig(TEST_PATTERN_SUB_CONFIG)) {
	TestPattern(valueOrDefault(TestPattern.ACTIVE, false),
			valueOrDefault(TestPattern.PATTERNS, TestPattern.DEFAULT_PATTERNS).toSet(),
			valueOrDefault(TestPattern.EXCLUDE_RULES, emptyList<String>()).toSet(),
			valueOrDefault(TestPattern.EXCLUDE_RULE_SETS, emptyList<String>()).toSet())
}

data class TestPattern(val active: Boolean,
					   private val _patterns: Set<String>,
					   val excludingRules: Set<String>,
					   private val excludingRuleSets: Set<String>) {

	private val patterns = _patterns.map { Regex(it) }

	fun matches(path: String) = patterns.any { it.matches(path) }
	fun matchesRuleSet(ruleSet: String) = excludingRuleSets.any { it == ruleSet }
	fun isTestSource(file: KtFile) = active && file.relativePath()?.let { matches(it) } == true

	companion object {
		const val TEST_PATTERN_SUB_CONFIG = "test-pattern"
		const val ACTIVE = "active"
		const val PATTERNS = "patterns"
		const val EXCLUDE_RULES = "exclude-rules"
		const val EXCLUDE_RULE_SETS = "exclude-rule-sets"

		val DEFAULT_PATTERNS = listOf(".*/test/.*Test.kt")
	}
}

