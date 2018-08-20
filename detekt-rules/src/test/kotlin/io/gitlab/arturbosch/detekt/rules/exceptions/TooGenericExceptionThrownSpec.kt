package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

/**
 * @author Artur Bosch
 */
class TooGenericExceptionThrownSpec : Spek({

	describe("a file with many thrown exceptions") {

		it("should report one for each generic throw rules") {
			val rule = TooGenericExceptionThrown(Config.empty)

			val findings = rule.lint(Case.TooGenericExceptions.path())

			assertThat(findings).hasSize(thrownExceptionDefaults.size)
		}

		it("should not report thrown exceptions") {
			val config = TestConfig(mapOf(TooGenericExceptionThrown.THROWN_EXCEPTIONS_PROPERTY to "[MyException]"))
			val rule = TooGenericExceptionCaught(config)

			val findings = rule.lint(Case.TooGenericExceptions.path())

			assertThat(findings).isEmpty()
		}
	}
})
