package io.gitlab.arturbosch.detekt.core.rules

import io.github.detekt.test.utils.resourceAsPath
import io.github.detekt.tooling.api.spec.RulesSpec.RunPolicy.DisableDefaultRuleSets
import io.github.detekt.tooling.api.spec.RulesSpec.RunPolicy.RestrictToSingleRule
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.core.createNullLoggingSpec
import io.gitlab.arturbosch.detekt.core.tooling.withSettings
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class RuleSetsSpec {

    @Test
    fun `loads all the RuleSetProviders`() {
        val providers = createNullLoggingSpec().withSettings { createRuleProviders() }

        assertThat(providers.map { it.ruleSetId.value })
            .containsExactlyInAnyOrder(
                "sample-rule-set",
                *defaultRuleSets.toTypedArray(),
            )
    }

    @Test
    fun `loads all the DefaultRuleSetProviders except the disabled`() {
        val providers = createNullLoggingSpec { extensions { disableExtension("style") } }
            .withSettings { createRuleProviders() }

        assertThat(providers.map { it.ruleSetId.value })
            .containsExactlyInAnyOrder(
                "sample-rule-set",
                *defaultRuleSets.minus("style").toTypedArray(),
            )
    }

    @Test
    fun `does not load any default rule set provider when opt out`() {
        val providers = createNullLoggingSpec { rules { runPolicy = DisableDefaultRuleSets } }
            .withSettings { createRuleProviders() }

        assertThat(providers.map { it.ruleSetId.value })
            .containsExactlyInAnyOrder("sample-rule-set")
    }

    @ParameterizedTest
    @ValueSource(strings = ["MagicNumber", "MagicNumber/id"])
    fun `only loads the provider with the selected rule`(ruleId: String) {
        val providers = createNullLoggingSpec {
            rules { runPolicy = RestrictToSingleRule(RuleSet.Id("style"), Rule.Id(ruleId)) }
        }
            .withSettings { createRuleProviders() }

        assertThat(providers.map { it.ruleSetId.value })
            .containsExactlyInAnyOrder("style")
        assertThat(providers.single().instance().rules)
            .containsOnlyKeys(Rule.Name("MagicNumber"))
    }

    @ParameterizedTest
    @ValueSource(strings = ["MagicNumber", "MagicNumber/id"])
    fun `throws when rule set doesn't exist`(ruleId: String) {
        assertThatThrownBy {
            createNullLoggingSpec {
                rules { runPolicy = RestrictToSingleRule(RuleSet.Id("foo"), Rule.Id(ruleId)) }
            }
                .withSettings { createRuleProviders() }
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("There was no rule set with id 'foo'.")
    }

    /**
     * This test runs a precompiled jar with a custom rule provider.
     * When any breaking change in 'detekt-api' is done, this test will break.
     *
     * The procedure to repair this test is:
     *  1. 'gradle build -x test publishToMavenLocal'
     *  2. 'gradle build' again to let the 'sample' project pick up the new api changes.
     *  3. 'cp detekt-sample-extensions/build/libs/detekt-sample-extensions-<version>.jar
     *          detekt-core/src/test/resources/sample-rule-set.jar'
     *  4. Now 'gradle build' should be green again.
     */
    @Test
    fun `loads custom rule sets through jars`() {
        val providers = createNullLoggingSpec {
            extensions {
                fromPaths { listOf(resourceAsPath("sample-rule-set.jar")) }
            }
        }.withSettings { createRuleProviders() }

        assertThat(providers.map { it.ruleSetId.value })
            .containsExactlyInAnyOrder(
                "sample-rule-set",
                "sample",
                *defaultRuleSets.toTypedArray(),
            )
    }
}

val defaultRuleSets = listOf(
    "potential-bugs",
    "complexity",
    "coroutines",
    "comments",
    "empty-blocks",
    "exceptions",
    "naming",
    "performance",
    "style",
)
