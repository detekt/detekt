package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.style.naming.ForbiddenClassName
import io.gitlab.arturbosch.detekt.rules.style.naming.NamingRules
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it


class ForbiddenNameSpec: Spek({

	given("forbidden naming rules") {
		val configCustomRules =
				object : Config {
					override fun subConfig(key: String): Config = Config.empty

					@Suppress("UNCHECKED_CAST")
					override fun <T : Any> valueOrDefault(key: String, default: T): T =
							when (key) {
								ForbiddenClassName.FORBIDDEN_NAME -> "Manager ,  Provider" as T
								else -> default
							}
				}
		val config = object : Config {
			override fun subConfig(key: String): Config =
					when (key) {
						ForbiddenClassName::class.simpleName -> configCustomRules
						else -> Config.empty
					}

			override fun <T : Any> valueOrDefault(key: String, default: T): T = default
		}

		it("should report classes with forbidden names") {
			Assertions.assertThat(NamingRules(config).lint("""
			class TestManager {
				fun getTest() {
					return "Test"
				}
			}
			""")).hasSize(1)
		}

		it("should report another class with a forbidden name") {
			Assertions.assertThat(NamingRules(config).lint("""
			class TestProvider {
				fun getTest() {
					return "Test"
				}
			}
			""")).hasSize(1)
		}

		it("should not report classes that don't contain any forbidden names") {
			Assertions.assertThat(NamingRules(config).lint("""
			class TestHolder {
				fun getTest() {
					return "Test"
				}
			}
			""")).hasSize(0)
		}
	}
})
