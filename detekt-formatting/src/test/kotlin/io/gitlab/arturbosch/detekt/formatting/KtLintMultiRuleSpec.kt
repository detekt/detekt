package io.gitlab.arturbosch.detekt.formatting

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.api.Config
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class KtLintMultiRuleSpec {

    @Nested
    inner class `KtLintMultiRule rule` {

        @Test
        fun `sorts rules correctly`() {
            val ktlintRule = KtLintMultiRule(Config.empty)
            ktlintRule.visitFile(compileContentForTest(""))
            val sortedRules = ktlintRule.getSortedRules()
            assertThat(sortedRules).isNotEmpty
            assertThat(sortedRules.indexOfFirst { it.runOnRootNodeOnly })
                .isGreaterThan(-1)
                .isLessThan(sortedRules.indexOfFirst { !it.runOnRootNodeOnly })
            assertThat(sortedRules.indexOfFirst { !it.runOnRootNodeOnly })
                .isGreaterThan(-1)
                .isLessThan(sortedRules.indexOfFirst { it.runOnRootNodeOnly && it.runAsLateAsPossible })
            assertThat(sortedRules.indexOfFirst { it.runOnRootNodeOnly && it.runAsLateAsPossible })
                .isGreaterThan(-1)
                .isLessThan(sortedRules.indexOfFirst { it.runAsLateAsPossible && !it.runOnRootNodeOnly })
        }
    }
}
