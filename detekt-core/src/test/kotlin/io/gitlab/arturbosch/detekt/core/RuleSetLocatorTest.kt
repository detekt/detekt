package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.reflections.Reflections
import java.lang.reflect.Modifier

class RuleSetLocatorTest {

	private val packageName = "io.gitlab.arturbosch.detekt.rules.providers"

	@Test
	fun containsAllRuleProviders() {
		val locator = RuleSetLocator(ProcessingSettings(path))
		val providers = locator.load()
		val providerClasses = getProviderClasses()

		assertThat(providerClasses).isNotEmpty
		providerClasses
				.filter { clazz -> providers.firstOrNull { it.javaClass == clazz } == null }
				.forEach { Assertions.fail("$it rule set is not loaded by the RuleSetLocator") }
	}

	private fun getProviderClasses(): List<Class<out RuleSetProvider>> {
		return Reflections(packageName)
				.getSubTypesOf(RuleSetProvider::class.java)
				.filter { !Modifier.isAbstract(it.modifiers) }
	}
}
