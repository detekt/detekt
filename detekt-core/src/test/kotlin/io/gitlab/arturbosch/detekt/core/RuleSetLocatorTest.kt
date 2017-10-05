package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.RuleSetProvider
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
		val classes = getClasses()

		assertThat(classes).isNotEmpty
		classes
				.map { c -> providers.firstOrNull { it.javaClass == c } }
				.forEach { assertThat(it).isNotNull() }
	}

	private fun getClasses(): List<Class<out RuleSetProvider>> {
		return Reflections(packageName)
				.getSubTypesOf(RuleSetProvider::class.java)
				.filter { !Modifier.isAbstract(it.modifiers) }
	}
}
