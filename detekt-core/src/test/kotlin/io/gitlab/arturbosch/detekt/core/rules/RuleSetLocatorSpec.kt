package io.gitlab.arturbosch.detekt.core.rules

import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.core.createNullLoggingSpec
import io.gitlab.arturbosch.detekt.core.tooling.withSettings
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.reflections.Reflections
import java.lang.reflect.Modifier

internal class RuleSetLocatorSpec {

    @Nested
    inner class `locating RuleSetProvider's` {

        @Test
        fun `contains all RuleSetProviders`() {
            val providers = createNullLoggingSpec().withSettings { RuleSetLocator(this).load() }
            val providerClasses = getProviderClasses()

            assertThat(providerClasses).isNotEmpty
            providerClasses
                .filter { clazz -> providers.none { it.javaClass == clazz } }
                .forEach { fail("$it rule set is not loaded by the RuleSetLocator") }
        }

        @Test
        fun `does not load any default rule set provider when opt out`() {
            val providers = createNullLoggingSpec { extensions { disableDefaultRuleSets = true } }
                .withSettings { RuleSetLocator(this).load() }

            val defaultProviders = getProviderClasses().toSet()

            assertThat(providers).noneMatch { it.javaClass in defaultProviders }
        }
    }
}

private fun getProviderClasses(): List<Class<out RuleSetProvider>> =
    Reflections("io.gitlab.arturbosch.detekt.rules")
        .getSubTypesOf(RuleSetProvider::class.java)
        .filter { !Modifier.isAbstract(it.modifiers) }
