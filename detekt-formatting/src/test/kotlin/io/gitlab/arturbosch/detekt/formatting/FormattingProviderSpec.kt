package io.gitlab.arturbosch.detekt.formatting

import com.pinterest.ktlint.rule.engine.core.api.Rule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FormattingProviderSpec {

    @Test
    fun `list is unique`() {
        val subject: RuleSet = FormattingProvider().instance(Config.empty)
        subject.rules.groupBy { it.ruleId }.forEach {
            assertThat(it.value).hasSize(1)
        }
    }

    @Test
    fun `run as late as possible is observed`() {
        val subject: RuleSet = FormattingProvider().instance(Config.empty)
        val formattingRules = subject.rules.map { it as FormattingRule }
        val indexOfFirstLateRule = formattingRules.indexOfFirst { it.runAsLateAsPossible }
        assertThat(indexOfFirstLateRule).isGreaterThan(0)
    }

    @Test
    fun `run after rule is observed`() {
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
                    assertThat(ruleIdToIndices[formattingRule.wrappingRuleId])
                        .describedAs("${formattingRule.wrappingRuleId} should run after ${runAfterRule.ruleId}")
                        .isGreaterThan(ruleIdToIndices[runAfterRule.ruleId])
                }
        }
    }
}
