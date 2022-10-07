package io.gitlab.arturbosch.detekt.formatting

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.api.Config
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KtLintMultiRuleSpec {

    @Test
    fun `sorts rules correctly`() {
        val ktlintRule = KtLintMultiRule(Config.empty)
        ktlintRule.visitFile(compileContentForTest(""))
        val sortedRules = ktlintRule.getSortedRules()
        assertThat(sortedRules).isNotEmpty
        assertThat(sortedRules.indexOfFirst { !it.runAsLateAsPossible })
            .isGreaterThan(-1)
            .isLessThan(sortedRules.indexOfFirst { it.runAsLateAsPossible })
    }
}
