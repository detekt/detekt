package io.gitlab.arturbosch.detekt.core.extensions

import io.gitlab.arturbosch.detekt.api.ConfigValidator
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.core.createNullLoggingSpec
import io.gitlab.arturbosch.detekt.core.rules.RuleSetLocator
import io.gitlab.arturbosch.detekt.core.tooling.withSettings
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LoadingSpec {

    @Test
    fun `extensions can be excluded via ExtensionSpec`() {
        val providers = createNullLoggingSpec {
            extensions {
                disableExtension("SampleConfigValidator")
            }
        }.withSettings { loadExtensions<ConfigValidator>(this) }

        assertThat(providers.map { it.id })
            .doesNotContain("SampleConfigValidator")
    }

    @Test
    fun `RuleSetProvider can be excluded via ExtensionSpec`() {
        val providers = createNullLoggingSpec {
            extensions {
                disableExtension("sample-rule-set")
            }
        }.withSettings { RuleSetLocator(this).load() }

        assertThat(providers.map { it.ruleSetId })
            .doesNotContain(RuleSet.Id("sample-rule-set"))
    }
}
