package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileForTest
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class TrailingWhitespaceSpec : Spek({

    describe("TrailingWhitespace rule") {

        context("a kt file that contains lines that end with a whitespace") {
            val rule = TrailingWhitespace()
            val file = compileForTest(Case.TrailingWhitespacePositive.path())
            val lines = file.text.splitToSequence("\n")
            rule.visit(KtFileContent(file, lines))

            it("should flag it") {
                assertThat(rule.findings).hasSize(7)
            }

            it("should report the correct source location") {
                assertThat(rule.findings).hasSourceLocations(
                    SourceLocation(4, 1),
                    SourceLocation(6, 13),
                    SourceLocation(8, 35),
                    SourceLocation(10, 1),
                    SourceLocation(12, 20),
                    SourceLocation(14, 23),
                    SourceLocation(16, 3)
                )
            }

            it("should report the correct text location") {
                assertThat(rule.findings).hasTextLocations(
                    55 to 56,
                    114 to 115,
                    163 to 164,
                    204 to 208,
                    245 to 246,
                    311 to 312,
                    329 to 331
                )
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
