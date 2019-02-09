package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileContentForTest
import io.gitlab.arturbosch.detekt.test.lint
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

class UnderscoresInNumericLiteralsSpec : Spek({
	given("an Int of 1000") {
		val ktFile = compileContentForTest("val myInt = 1000")

		it("should be reported by default") {
			val findings = UnderscoresInNumericLiterals().lint(ktFile)
			assertThat(findings).isNotEmpty
		}

		it("should not be reported if minAcceptableLength is 5") {
			val findings = UnderscoresInNumericLiterals(
					TestConfig(mapOf(UnderscoresInNumericLiterals.MIN_ACCEPTABLE_LENGTH to "5"))
			).lint(ktFile)
			assertThat(findings).isEmpty()
		}
	}

	given("an Int of 1_000_000") {
		val ktFile = compileContentForTest("val myInt = 1_000_000")

		it("should not be reported") {
			val findings = UnderscoresInNumericLiterals().lint(ktFile)
			assertThat(findings).isEmpty()
		}
	}
})
