package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

class TrailingWhitespaceSpec : Spek({

	given("a kt file that contains lines that end with a whitespace") {
		it("should flag it") {
			val rule = TrailingWhitespace()
			rule.visit(Case.TrailingWhitespacePositive.getKtFileContent())
			assertThat(rule.findings).hasSize(7)
		}
	}

	given("a kt file that does not contain lines that end with a whitespace") {
		it("should not flag it") {
			val rule = TrailingWhitespace()
			rule.visit(Case.TrailingWhitespaceNegative.getKtFileContent())
			assertThat(rule.findings).hasSize(0)
		}
	}
})
