package io.gitlab.arturbosch.detekt.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LoadDeprecatedRuleIdsSpec {
    @Test
    fun loadDeprecatedRules() {
        // rule id as used by config path mechanism uses 'ruleSetId > ruleId' pattern (with space)
        val ruleIdPattern = """^[-\w]+ > \w+$""".toRegex()

        val actual = loadDeprecatedRuleIds()

        assertThat(actual)
            .isNotEmpty()
            .allMatch { ruleId -> ruleIdPattern.matches(ruleId as String) }
    }
}
