package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.reflections.Reflections
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.lang.reflect.Modifier

class RuleSetLocatorTest : Spek({
    describe("Reulset") {

        it("containsAllRuleProviders") {
            val locator = RuleSetLocator(ProcessingSettings(path))
            val providers = locator.load()
            val providerClasses = getProviderClasses()

            assertThat(providerClasses).isNotEmpty
            providerClasses
                    .filter { clazz -> providers.firstOrNull { it.javaClass == clazz } == null }
                    .forEach { fail("$it rule set is not loaded by the RuleSetLocator") }
        }
    }
})

private fun getProviderClasses(): List<Class<out RuleSetProvider>> {
    return Reflections("io.gitlab.arturbosch.detekt.rules.providers")
            .getSubTypesOf(RuleSetProvider::class.java)
            .filter { !Modifier.isAbstract(it.modifiers) }
}
