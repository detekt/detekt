package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.compileForTest
import org.assertj.core.api.Assertions.assertThat
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

            it("should report the correct source location for a comment with trailing whitespace") {
                val findingSource = rule.findings[1].location.source
                assertThat(findingSource.line).isEqualTo(5)
                assertThat(findingSource.column).isEqualTo(1)
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
