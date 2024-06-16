package io.gitlab.arturbosch.detekt.core.rules

import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.core.createNullLoggingSpec
import io.gitlab.arturbosch.detekt.core.tooling.withSettings
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RuleSetLocatorSpec {

    @Test
    fun `loads all the RuleSetProviders`() {
        val providers = createNullLoggingSpec().withSettings { RuleSetLocator(this).load() }

        assertThat(providers.map { it.ruleSetId.value })
            .containsExactlyInAnyOrder(
                "sample-rule-set",
                *defaultRuleSets.toTypedArray(),
            )
    }

    @Test
    fun `loads all the DefaultRuleSetProviders except the disabled`() {
        val providers = createNullLoggingSpec { extensions { disableExtension("style") } }
            .withSettings { RuleSetLocator(this).load() }

        assertThat(providers.map { it.ruleSetId.value })
            .containsExactlyInAnyOrder(
                "sample-rule-set",
                *defaultRuleSets.minus("style").toTypedArray(),
            )
    }

    @Test
    fun `does not load any default rule set provider when opt out`() {
        val providers = createNullLoggingSpec { extensions { disableDefaultRuleSets = true } }
            .withSettings { RuleSetLocator(this).load() }

        assertThat(providers.map { it.ruleSetId.value })
            .containsExactlyInAnyOrder("sample-rule-set")
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
        }.withSettings { RuleSetLocator(this).load() }

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
