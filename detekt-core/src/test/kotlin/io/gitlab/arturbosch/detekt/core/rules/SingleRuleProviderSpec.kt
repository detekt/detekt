package io.gitlab.arturbosch.detekt.core.rules

import dev.detekt.api.Config
import dev.detekt.api.Rule
import dev.detekt.api.RuleName
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetProvider
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class SingleRuleProviderSpec {
    @ParameterizedTest
    @ValueSource(strings = ["CustomRule1", "CustomRule2"])
    fun `constructs the expected RuleSetProvider`(ruleNameString: String) {
        val ruleName = RuleName(ruleNameString)
        val wrappedRuleSet = CustomRuleSetProvider()
        val subject = SingleRuleProvider(ruleName, wrappedRuleSet)
        assertThat(subject.ruleSetId.value).isEqualTo("custom")
        assertThat(subject.instance().id.value).isEqualTo("custom")
        assertThat(subject.instance().rules).containsOnlyKeys(ruleName)
        assertThat(subject.instance().rules[ruleName]).isSameAs(wrappedRuleSet.instance().rules[ruleName])
    }

    @Test
    fun `throws when the rule name doesn't exist`() {
        assertThatThrownBy {
            SingleRuleProvider(RuleName("ARule"), CustomRuleSetProvider())
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("There was not rule 'ARule' in rule set 'custom'.")
    }
}

private class CustomRuleSetProvider : RuleSetProvider {
    override val ruleSetId = RuleSet.Id("custom")
    override fun instance() = RuleSet(
        ruleSetId,
        listOf(::CustomRule1, ::CustomRule2)
    )
}

private class CustomRule1(config: Config) : Rule(config, "TestDescription")
private class CustomRule2(config: Config) : Rule(config, "TestDescription")
