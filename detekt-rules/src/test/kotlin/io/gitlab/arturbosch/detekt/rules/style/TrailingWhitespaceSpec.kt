package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.assertThat
import io.github.detekt.test.utils.compileContentForTest
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class TrailingWhitespaceSpec : Spek({

    val rule by memoized { TrailingWhitespace() }

    describe("TrailingWhitespace rule") {

        context("positive cases") {

            it("reports a line just with a whitespace") {
                rule.visit(" ".toKtFileContent())
                assertThat(rule.findings).hasTextLocations(0 to 1)
            }

            it("reports a commented line with a whitespace at the end") {
                rule.visit("// A comment ".toKtFileContent())
                assertThat(rule.findings).hasTextLocations(12 to 13)
            }

            it("reports a class declaration with a whitespace at the end") {
                rule.visit("  class TrailingWhitespacePositive { \n  }".toKtFileContent())
                assertThat(rule.findings).hasTextLocations(36 to 37)
            }

            it("reports a print statement with a tab at the end") {
                rule.visit("\t\tprintln(\"A message\")\t".toKtFileContent())
                assertThat(rule.findings).hasTextLocations(22 to 23)
            }
        }

        context("negative cases") {

            it("does not report a class and function declaration with no whitespaces at the end") {
                val code = """
                    class C {

                        fun f() {
                            println("A message")
                            println("Another message") ;
                        }
                    }
                """.trimIndent()
                rule.visit(code.toKtFileContent())
                assertThat(rule.findings).isEmpty()
            }

            it("does not report an indentation inside multi-line strings") {
                val code = """
                    val multiLineStringWithIndents = ""${'"'}
                        Should ignore indent on the next line
                        
                        Should ignore indent on the previous line
                    ""${'"'}
                """.trim()
                rule.visit(code.toKtFileContent())
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
