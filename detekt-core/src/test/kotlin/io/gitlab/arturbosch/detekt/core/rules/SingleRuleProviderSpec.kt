package io.gitlab.arturbosch.detekt.core.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.test.yamlConfigFromContent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class SingleRuleProviderSpec {

    private val provider = SingleRuleProvider(
        "MagicNumber",
        object : RuleSetProvider {
            override val ruleSetId: String = "style"
            override fun instance(config: Config): RuleSet {
                val rule = object : Rule(config) {
                    override val issue = Issue(
                        "MagicNumber",
                        Severity.CodeSmell,
                        "",
                        Debt.FIVE_MINS
                    )
                }
                return RuleSet(ruleSetId, listOf(rule))
            }
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
    provider.instance(config.subConfig("style")).rules.first() as Rule
