package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleId
import io.gitlab.arturbosch.detekt.api.internal.absolutePath
import io.gitlab.arturbosch.detekt.core.TestPattern.Companion.TEST_PATTERN_SUB_CONFIG
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Artur Bosch
 */

fun createTestPattern(
    config: Config,
    root: Path = Paths.get("").toAbsolutePath()): TestPattern {
    return with(config.subConfig(TEST_PATTERN_SUB_CONFIG)) {
        TestPattern(valueOrDefault(TestPattern.ACTIVE, false),
                valueOrDefault(TestPattern.PATTERNS, TestPattern.DEFAULT_PATTERNS).toSet(),
                valueOrDefault(TestPattern.EXCLUDE_RULES, emptyList<RuleId>()).toSet(),
                valueOrDefault(TestPattern.EXCLUDE_RULE_SETS, emptyList<String>()).toSet(),
                root)
    }
}

data class TestPattern(
    val active: Boolean,
    val patterns: Set<String>,
    val excludingRules: Set<RuleId>,
    private val excludingRuleSets: Set<String>,
    private val root: Path
) {

    private val _patterns = patterns.map { PathFilter(it, root) }

    fun matches(path: String) = _patterns.any { it.matches(Paths.get(path)) }
    fun matchesRuleSet(ruleSet: String) = excludingRuleSets.any { it == ruleSet }
    fun isTestSource(file: KtFile) = active && file.absolutePath()?.let { matches(it) } == true

    companion object {
        const val TEST_PATTERN_SUB_CONFIG = "test-pattern"
        const val ACTIVE = "active"
        const val PATTERNS = "patterns"
        const val EXCLUDE_RULES = "exclude-rules"
        const val EXCLUDE_RULE_SETS = "exclude-rule-sets"

        val DEFAULT_PATTERNS = listOf(".*/test/.*Test.kt")
    }
}
