package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import java.lang.reflect.Modifier
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import org.reflections.Reflections

class RuleSetLocatorTest : Spek({

    it("containsAllRuleProviders") {
        val locator = RuleSetLocator(ProcessingSettings(path))
        val providers = locator.load()
        val providerClasses = getProviderClasses()

        assertThat(providerClasses).isNotEmpty
        providerClasses
                .filter { clazz -> providers.firstOrNull { it.javaClass == clazz } == null }
                .forEach { Assertions.fail("$it rule set is not loaded by the RuleSetLocator") }
    }
})

private fun getProviderClasses(): List<Class<out RuleSetProvider>> {
    return Reflections("io.gitlab.arturbosch.detekt.rules.providers")
            .getSubTypesOf(RuleSetProvider::class.java)
            .filter { !Modifier.isAbstract(it.modifiers) }
}
