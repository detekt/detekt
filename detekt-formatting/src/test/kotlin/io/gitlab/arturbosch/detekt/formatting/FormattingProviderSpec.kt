package io.gitlab.arturbosch.detekt.formatting

import com.pinterest.ktlint.rule.engine.core.api.Rule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FormattingProviderSpec {

    @Test
    fun `run as late as possible is observed`() {
        val subject: RuleSet = FormattingProvider().instance(Config.empty)
        val formattingRules = subject.rules.map { it as FormattingRule }
        val indexOfFirstLateRule = formattingRules.indexOfFirst { it.runAsLateAsPossible }
        val indexOfLastRegularRule = formattingRules.indexOfLast { it.runAsLateAsPossible.not() }
        assertThat(indexOfFirstLateRule).isGreaterThan(indexOfLastRegularRule)
    }

    @Test
    fun `run as a as possible is observed`() {
        val subject: RuleSet = FormattingProvider().instance(Config.empty)
        val formattingRules = subject.rules.map { it as FormattingRule }
        val ruleIdToIndices = formattingRules
            .mapIndexed { index, formattingRule -> formattingRule.wrapping.ruleId to index }
            .toMap()

        formattingRules.forEach { formattingRule ->
            formattingRule
                .visitorModifiers
                .filterIsInstance<Rule.VisitorModifier.RunAfterRule>()
                .forEach { runAfterRule ->
                    assertThat(ruleIdToIndices[formattingRule.ktlintRuleId])
                        .describedAs("${formattingRule.ktlintRuleId} should run after ${runAfterRule.ruleId}")
                        .isGreaterThan(ruleIdToIndices[runAfterRule.ruleId])
                }
        }
    }
}
