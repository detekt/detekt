package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

class ForbiddenNameSpec : Spek({

	given("forbidden naming rules") {
		it("should report classes with forbidden names") {
			val code = """
				class TestManager {} // violation
				class TestProvider {} // violation
				class TestHolder"""
			assertThat(NamingRules(TestConfig(mapOf(ForbiddenClassName.FORBIDDEN_NAME to "Manager ,  Provider")))
					.lint(code))
					.hasSize(2)
		}

		it("should report a class that starts with a forbidden name") {
			val code = "class TestProvider {}"
			assertThat(NamingRules(TestConfig(mapOf(ForbiddenClassName.FORBIDDEN_NAME to "test")))
					.lint(code))
					.hasSize(1)
		}
	}
})
