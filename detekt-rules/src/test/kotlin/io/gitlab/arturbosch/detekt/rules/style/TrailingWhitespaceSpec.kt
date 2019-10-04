package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.api.TextLocation
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
                    SourceLocation(12, 23),
                    SourceLocation(14, 29),
                    SourceLocation(16, 6)
                )
            }

            it("should report the correct text location") {
                assertThat(rule.findings).hasTextLocation(
                    TextLocation(55, 56),
                    TextLocation(114, 115),
                    TextLocation(163, 164),
                    TextLocation(204, 208),
                    TextLocation(245, 246),
                    TextLocation(311, 312),
                    TextLocation(329, 331)
                )
            }
        }

        context("a kt file that does not contain lines that end with a whitespace") {

            it("should not flag it") {
                val rule = TrailingWhitespace()
                rule.visit(Case.TrailingWhitespaceNegative.getKtFileContent())
                assertThat(rule.findings).hasSize(0)
            }
        }
    }
})
