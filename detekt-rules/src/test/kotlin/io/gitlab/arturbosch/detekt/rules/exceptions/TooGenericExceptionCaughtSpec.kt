package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class TooGenericExceptionCaughtSpec : Spek({

	describe("a file with many caught exceptions") {

		it("should find one of each kind") {
			val rule = TooGenericExceptionCaught(Config.empty)

			val findings = rule.lint(Case.TooGenericExceptions.path())

			assertThat(findings).hasSize(caughtExceptionDefaults.size)
		}
	}

	describe("a file with a caught exception which is ignored") {

		it("should not report an ignored catch blocks because of its exception name") {
			val config = TestConfig(mapOf(TooGenericExceptionCaught.ALLOWED_EXCEPTION_NAME_REGEX to "myIgnore"))
			val rule = TooGenericExceptionCaught(config)

			val findings = rule.lint(Case.TooGenericExceptionsOptions.path())

			assertThat(findings).isEmpty()
		}
	}
})
