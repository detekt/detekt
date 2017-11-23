package io.gitlab.arturbosch.detekt.rules.style.naming

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

/**
 * @author Artur Bosch
 */
class EnumNamingSpec : Spek({

	describe("numbers are allowed in enum entries") {
		it("should detect no violation") {
			val findings = NamingRules().lint(
					"""
				enum class WorkFlow {
					ACTIVE, NOT_ACTIVE, Unknown, Number1
				}
				"""
			)
			Assertions.assertThat(findings).isEmpty()
		}
	}
})
