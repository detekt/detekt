package io.gitlab.arturbosch.detekt.generator.collection

import io.gitlab.arturbosch.detekt.test.KtTestCompiler
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertFailsWith

class RuleSetProviderCollectorSpec : Spek({

	val collector = RuleSetProviderCollector()
	fun <T> Collector<T>.run(code: String): List<T> {
		val ktFile = KtTestCompiler.compileFromContent(code.trimIndent())
		visit(ktFile)
		return items
	}

	given("a non-RuleSetProvider class extending nothing") {
		val code = """
			package foo

			class SomeRandomClass {
				fun logSomething(message: String) {
					println(message)
				}
			}
		"""
		it("collects no rulesets") {
			val items = collector.run(code)
			assertThat(items).isEmpty()
		}
	}

	given("a non-RuleSetProvider class extending a class that is not related to rules") {
		val code = """
			package foo

			class SomeRandomClass: SomeOtherClass {
				fun logSomething(message: String) {
					println(message)
				}
			}
		"""
		it("collects no rulesets") {
			val items = collector.run(code)
			assertThat(items).isEmpty()
		}
	}

	given("a correct RuleSetProvider class extending RuleSetProvider but missing parameters") {
		val code = """
			package foo

			class TestProvider: RuleSetProvider {
				fun logSomething(message: String) {
					println(message)
				}
			}
		"""

		it("collects a RuleSetProvider") {
			val items = collector.run(code)
			assertThat(items).hasSize(1)
		}

		it("has no rules") {
			val items = collector.run(code)
			val provider = items[0]
			assertThat(provider.rules).isEmpty()
		}

		it("has no name") {
			val items = collector.run(code)
			val provider = items[0]
			assertThat(provider.name).isEmpty()
		}

		it("has no description") {
			val items = collector.run(code)
			val provider = items[0]
			assertThat(provider.description).isEmpty()
		}
	}

	given("a correct RuleSetProvider class with full parameters") {
		val description = "This is a description"
		val ruleSetId = "test"
		val ruleName = "TestRule"
		val code = """
			package foo

			/**
			 * $description
			 *
			 * @active since v1.0.0
			 */
			class TestProvider: RuleSetProvider {
				override val ruleSetId: String = "$ruleSetId"

				override fun instance(config: Config): RuleSet {
					return RuleSet(ruleSetId, listOf(
							$ruleName(config)
					))
				}
			}
		"""

		it("collects a RuleSetProvider") {
			val items = collector.run(code)
			assertThat(items).hasSize(1)
		}

		it("has one rule") {
			val items = collector.run(code)
			val provider = items[0]
			assertThat(provider.rules).hasSize(1)
			assertThat(provider.rules[0]).isEqualTo(ruleName)
		}

		it("has correct name") {
			val items = collector.run(code)
			val provider = items[0]
			assertThat(provider.name).isEqualTo(ruleSetId)
		}

		it("has correct description") {
			val items = collector.run(code)
			val provider = items[0]
			assertThat(provider.description).isEqualTo(description)
		}

		it("is active") {
			val items = collector.run(code)
			val provider = items[0]
			assertThat(provider.active).isTrue()
		}
	}

	given("an inactive RuleSetProvider") {
		val description = "This is a description"
		val ruleSetId = "test"
		val ruleName = "TestRule"
		val code = """
			package foo

			/**
			 * $description
			 */
			class TestProvider: RuleSetProvider {
				override val ruleSetId: String = "$ruleSetId"

				override fun instance(config: Config): RuleSet {
					return RuleSet(ruleSetId, listOf(
							$ruleName(config)
					))
				}
			}
		"""

		it("is not active") {
			val items = collector.run(code)
			val provider = items[0]
			assertThat(provider.active).isFalse()
		}
	}

	given("a RuleSetProvider with missing name") {
		val description = "This is a description"
		val ruleName = "TestRule"
		val code = """
			package foo

			/**
			 * $description
			 */
			class TestProvider: RuleSetProvider {
				override fun instance(config: Config): RuleSet {
					return RuleSet(ruleSetId, listOf(
							$ruleName(config)
					))
				}
			}
		"""

		it("is has no name") {
			val items = collector.run(code)
			val provider = items[0]
			assertThat(provider.name).isEmpty()
		}
	}

	given("a RuleSetProvider with missing description") {
		val ruleSetId = "test"
		val ruleName = "TestRule"
		val code = """
			package foo

			class TestProvider: RuleSetProvider {
				override val ruleSetId: String = "$ruleSetId"

				override fun instance(config: Config): RuleSet {
					return RuleSet(ruleSetId, listOf(
							$ruleName(config)
					))
				}
			}
		"""

		it("is not active") {
			val items = collector.run(code)
			val provider = items[0]
			assertThat(provider.description).isEmpty()
		}
	}

	given("a RuleSetProvider with no rules") {
		val ruleSetId = "test"
		val code = """
			package foo

			class TestProvider: RuleSetProvider {
				override val ruleSetId: String = "$ruleSetId"

				override fun instance(config: Config): RuleSet {
					return RuleSet(ruleSetId, emptyListOf())
				}
			}
		"""

		it("throws an exception") {
			assertFailsWith<InvalidRuleSetProviderException> {
				collector.run(code)
			}
		}
	}

	given("a correct RuleSetProvider class with full parameters") {
		val description = "This is a description"
		val ruleSetId = "test"
		val ruleName = "TestRule"
		val secondRuleName = "SecondRule"
		val code = """
			package foo

			/**
			 * $description
			 *
			 * @active since v1.0.0
			 */
			class TestProvider: RuleSetProvider {
				override val ruleSetId: String = "$ruleSetId"

				override fun instance(config: Config): RuleSet {
					return RuleSet(ruleSetId, listOf(
							$ruleName(config),
							$secondRuleName(config)
					))
				}
			}
		"""

		it("collects multiple rules") {
			val items = collector.run(code)
			assertThat(items[0].rules).containsExactly(ruleName, secondRuleName)
		}
	}
})
