package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.YamlConfig
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.rules.providers.ExceptionsProvider
import io.gitlab.arturbosch.detekt.test.compileForTest
import io.gitlab.arturbosch.detekt.test.lint
import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

/**
 * @author Artur Bosch
 */
class TooGenericExceptionThrownSpec : Spek({

	val file = compileForTest(Case.TooGenericExceptions.path())

	describe("a file with many caught exceptions") {

		it("should find one of each kind") {
			val rule = TooGenericExceptionCaught(Config.empty)

			val findings = rule.lint(file.text)

			assertThat(findings).hasSize(caughtExceptionDefaults.size)
		}
	}

	describe("a file with many thrown exceptions") {

		it("should report one for each generic throw rules") {
			val rule = TooGenericExceptionThrown(Config.empty)

			val findings = rule.lint(file.text)

			assertThat(findings).hasSize(thrownExceptionDefaults.size)
		}
	}

	it("should not report any as all catch exception rules are deactivated") {
		val config = YamlConfig.loadResource(resource("deactivated-exceptions.yml").toURL())
		val ruleSet = ExceptionsProvider().buildRuleset(config)

		val findings = ruleSet?.accept(file)

		assertThat(findings).hasSize(0)
	}
})
