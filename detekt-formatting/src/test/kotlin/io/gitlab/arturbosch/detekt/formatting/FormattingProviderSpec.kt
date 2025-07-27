package io.gitlab.arturbosch.detekt.formatting

import com.pinterest.ktlint.rule.engine.core.api.Rule
import dev.detekt.api.Config
import dev.detekt.api.RuleSet
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FormattingProviderSpec {

    @Test
    fun `run as late as possible is observed`() {
        val subject: RuleSet = FormattingProvider().instance()
        val formattingRules = subject.rules.map { (_, provider) -> provider(Config.empty) as FormattingRule }
        val indexOfFirstLateRule = formattingRules.indexOfFirst { it.runAsLateAsPossible }
        assertThat(indexOfFirstLateRule).isGreaterThan(0)
    }

    @Test
    fun `run after rule is observed`() {
        val subject: RuleSet = FormattingProvider().instance()
        val formattingRules = subject.rules.map { (_, provider) -> provider(Config.empty) as FormattingRule }
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
