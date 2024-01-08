package io.gitlab.arturbosch.detekt.core.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.test.yamlConfigFromContent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class SingleRuleProviderSpec {

    private val provider = SingleRuleProvider(
        "MagicNumber",
        object : RuleSetProvider {
            override val ruleSetId: String = "style"

            override fun instance(): RuleSet = RuleSet(ruleSetId, listOf(::MagicNumber))
        }
    )

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `the right sub config is passed to the rule configures rule with active=$value`(value: Boolean) {
        val config = yamlConfigFromContent(
            """
                style:
                  MagicNumber:
                    active: $value
            """.trimIndent()
        )

        assertThat(produceRule(provider, config).active).isEqualTo(value)
    }
}

private fun produceRule(provider: RuleSetProvider, config: Config): Rule =
    provider.instance().rules
        .map { (ruleId, provider) -> provider(config.subConfig("style").subConfig(ruleId)) }
        .single() as Rule

private class MagicNumber(config: Config) : Rule(config) {
    override val issue = Issue(javaClass.simpleName, "")
}
