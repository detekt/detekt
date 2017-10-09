package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.BaseRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.MultiRule
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.rules.providers.CommentSmellProvider
import io.gitlab.arturbosch.detekt.rules.providers.ComplexityProvider
import io.gitlab.arturbosch.detekt.rules.providers.EmptyCodeProvider
import io.gitlab.arturbosch.detekt.rules.providers.ExceptionsProvider
import io.gitlab.arturbosch.detekt.rules.providers.PerformanceProvider
import io.gitlab.arturbosch.detekt.rules.providers.PotentialBugProvider
import io.gitlab.arturbosch.detekt.rules.providers.StyleGuideProvider
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.reflections.Reflections
import java.lang.reflect.Modifier


class RuleProviderTest {

	@Test
	fun commentSmellProvider() {
		RuleProviderAssert(
				CommentSmellProvider(),
				"io.gitlab.arturbosch.detekt.rules.documentation",
				Rule::class.java)
				.assert()
	}

	@Test
	fun complexityProvider() {
		RuleProviderAssert(
				ComplexityProvider(),
				"io.gitlab.arturbosch.detekt.rules.complexity",
				Rule::class.java)
				.assert()
	}

	@Test
	fun emptyCodeProvider() {
		RuleProviderAssert(
				EmptyCodeProvider(),
				"io.gitlab.arturbosch.detekt.rules.empty",
				MultiRule::class.java)
				.assert()
	}

	@Test
	fun exceptionsProvider() {
		RuleProviderAssert(
				ExceptionsProvider(),
				"io.gitlab.arturbosch.detekt.rules.exceptions",
				Rule::class.java)
				.assert()
	}

	@Test
	fun performanceProvider() {
		RuleProviderAssert(
				PerformanceProvider(),
				"io.gitlab.arturbosch.detekt.rules.performance",
				Rule::class.java)
				.assert()
	}

	@Test
	fun potentialBugProvider() {
		RuleProviderAssert(
				PotentialBugProvider(),
				"io.gitlab.arturbosch.detekt.rules.bugs",
				Rule::class.java)
				.assert()
	}

	@Test
	fun styleGuideProvider() {
		RuleProviderAssert(
				StyleGuideProvider(),
				"io.gitlab.arturbosch.detekt.rules.style",
				Rule::class.java)
				.assert()
	}

	class RuleProviderAssert<T>
	(private val provider: RuleSetProvider,
	 private val packageName: String,
	 private val clazz: Class<T>) {

		fun assert() {
			val rules = getRules(provider)
			assertThat(rules).isNotEmpty
			val ruleClasses = getRuleClasses()
			assertThat(ruleClasses).isNotEmpty

			ruleClasses
					.filter { ruleClass -> rules.singleOrNull { it.javaClass == ruleClass } == null }
					.forEach { Assertions.fail("${it.simpleName} rule is not defined in the rules provider") }
		}

		private fun getRules(provider: RuleSetProvider): List<BaseRule> {
			return provider.buildRuleset(Config.empty)!!.rules
		}

		private fun getRuleClasses(): List<Class<out T>> {
			return Reflections(packageName)
					.getSubTypesOf(clazz)
					.filter { !Modifier.isAbstract(it.modifiers) && it.superclass.simpleName != "SubRule" }
		}
	}
}
