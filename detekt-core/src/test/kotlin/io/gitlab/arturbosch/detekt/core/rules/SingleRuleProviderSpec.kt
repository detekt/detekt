package io.gitlab.arturbosch.detekt.core.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class SingleRuleProviderSpec {
    @ParameterizedTest
    @ValueSource(strings = ["CustomRule1", "CustomRule2"])
    fun `constructs the expected RuleSetProvider`(ruleNameString: String) {
        val ruleName = Rule.Name(ruleNameString)
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
            SingleRuleProvider(Rule.Name("ARule"), CustomRuleSetProvider())
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
