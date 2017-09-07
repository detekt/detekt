package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.style.naming.ForbiddenClassName
import io.gitlab.arturbosch.detekt.rules.style.naming.NamingRules
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it


class ForbiddenNameSpec : Spek({

	given("forbidden naming rules") {
		it("should report classes with forbidden names") {
			assertThat(NamingRules(TestConfig(mapOf(ForbiddenClassName.FORBIDDEN_NAME to "Manager ,  Provider")))
					.lint("""
			class TestManager {
				fun getTest() {
					return "Test"
				}
			}
			""")).hasSize(1)
		}

		it("should report another class with a forbidden name") {
			assertThat(NamingRules(TestConfig(mapOf(ForbiddenClassName.FORBIDDEN_NAME to "Manager ,  Provider")))
					.lint("""
			class TestProvider {
				fun getTest() {
					return "Test"
				}
			}
			""")).hasSize(1)
		}

		it("should report a class that starts with a forbidden name") {
			assertThat(NamingRules(TestConfig(mapOf(ForbiddenClassName.FORBIDDEN_NAME to "test")))
					.lint("""
			class TestProvider {
				fun getTest() {
					return "Test"
				}
			}
			""")).hasSize(1)
		}

		it("should not report classes that don't contain any forbidden names") {
			assertThat(NamingRules(TestConfig(mapOf(ForbiddenClassName.FORBIDDEN_NAME to "Manager ,  Provider")))
					.lint("""
			class TestHolder {
				fun getTest() {
					return "Test"
				}
			}
			""")).hasSize(0)
		}
	}
})
