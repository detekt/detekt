package dev.detekt.rules.ktlintwrapper

import com.pinterest.ktlint.rule.engine.core.api.Rule
import dev.detekt.api.Config
import dev.detekt.api.RuleSet
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KtlintWrapperProviderSpec {

    @Test
    fun `run as late as possible is observed`() {
        val subject: RuleSet = KtlintWrapperProvider().instance()
        val ktlintRules = subject.rules.map { (_, provider) -> provider(Config.Empty) as KtlintRule }
        val indexOfFirstLateRule = ktlintRules.indexOfFirst { it.runAsLateAsPossible }
        assertThat(indexOfFirstLateRule).isGreaterThan(0)
    }

    @Test
    fun `run after rule is observed`() {
        val subject: RuleSet = KtlintWrapperProvider().instance()
        val ktlintRules = subject.rules.map { (_, provider) -> provider(Config.Empty) as KtlintRule }
        val ruleIdToIndices = ktlintRules
            .mapIndexed { index, ktlintRule -> ktlintRule.wrapping.ruleId to index }
            .toMap()

        ktlintRules.forEach { ktlintRule ->
            ktlintRule
                .visitorModifiers
                .filterIsInstance<Rule.VisitorModifier.RunAfterRule>()
                .forEach { runAfterRule ->
                    assertThat(ruleIdToIndices[ktlintRule.wrappingRuleId])
                        .describedAs("${ktlintRule.wrappingRuleId} should run after ${runAfterRule.ruleId}")
                        .isGreaterThan(ruleIdToIndices[runAfterRule.ruleId])
                }
        }
    }
}
