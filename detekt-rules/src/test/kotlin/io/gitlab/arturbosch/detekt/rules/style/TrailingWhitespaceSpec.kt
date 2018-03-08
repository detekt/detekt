package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileForTest
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

class TrailingWhitespaceSpec : Spek({

	given("a kt file that contains lines that end with a whitespace") {
		it("should flag it") {
			val rule = TrailingWhitespace()
			rule.visit(getKtFileContent(Case.TrailingWhitespacePositive))
			assertThat(rule.findings).hasSize(7)
		}
	}

	given("a kt file that does not contain lines that end with a whitespace") {
		it("should not flag it") {
			val rule = TrailingWhitespace()
			rule.visit(getKtFileContent(Case.TrailingWhitespaceNegative))
			assertThat(rule.findings).hasSize(0)
		}
	}
})

private fun getKtFileContent(case: Case): KtFileContent {
	val file = compileForTest(case.path())
	val lines = file.text.splitToSequence("\n")
	val ktFileContent = KtFileContent(file, lines)
	return ktFileContent
}
