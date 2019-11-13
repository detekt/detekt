package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileContentForTest
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class TrailingWhitespaceSpec : Spek({

    describe("TrailingWhitespace rule") {

        context("a line just with a whitespace") {
            val rule = TrailingWhitespace()
            rule.visit(" ".toKtFileContent())

            it("should report the correct text location") {
                assertThat(rule.findings).hasTextLocations(0 to 1)
            }
        }

        context("a commented line just with a whitespace") {
            val rule = TrailingWhitespace()
            rule.visit("// A comment ".toKtFileContent())

            it("should report the correct text location") {
                assertThat(rule.findings).hasTextLocations(12 to 13)
            }
        }

        context("a line with a whitespace") {
            val rule = TrailingWhitespace()
            rule.visit("  class TrailingWhitespacePositive { \n  }".toKtFileContent())

            it("should report the correct text location") {
                assertThat(rule.findings).hasTextLocations(36 to 37)
            }
        }

        context("a line with a whitespace") {
            val rule = TrailingWhitespace()
            rule.visit("\t\tprintln(\"A message\")\t".toKtFileContent())

            it("should report the correct text location") {
                assertThat(rule.findings).hasTextLocations(22 to 23)
            }
        }

        context("a kt file that does not contain lines that end with a whitespace") {

            it("should not flag it") {
                val rule = TrailingWhitespace()
                rule.visit(Case.TrailingWhitespaceNegative.getKtFileContent())
                assertThat(rule.findings).isEmpty()
            }
        }
    }
})

private fun String.toKtFileContent(): KtFileContent {
    val file = compileContentForTest(this)
    val lines = file.text.splitToSequence("\n")
    return KtFileContent(file, lines)
}
