package io.gitlab.arturbosch.detekt.rules.style

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.test.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class TrailingWhitespaceSpec {

    @Nested
    inner class `TrailingWhitespace rule` {

        @Nested
        inner class `positive cases` {

            @Test
            fun `reports a line just with a whitespace`() {
                val rule = TrailingWhitespace()
                rule.visit(" ".toKtFileContent())
                assertThat(rule.findings).hasTextLocations(0 to 1)
            }

            @Test
            fun `reports a commented line with a whitespace at the end`() {
                val rule = TrailingWhitespace()
                rule.visit("// A comment ".toKtFileContent())
                assertThat(rule.findings).hasTextLocations(12 to 13)
            }

            @Test
            fun `reports a class declaration with a whitespace at the end`() {
                val rule = TrailingWhitespace()
                rule.visit("  class TrailingWhitespacePositive { \n  }".toKtFileContent())
                assertThat(rule.findings).hasTextLocations(36 to 37)
            }

            @Test
            fun `reports a print statement with a tab at the end`() {
                val rule = TrailingWhitespace()
                rule.visit("\t\tprintln(\"A message\")\t".toKtFileContent())
                assertThat(rule.findings).hasTextLocations(22 to 23)
            }
        }

        @Nested
        inner class `negative cases` {

            @Test
            fun `does not report a class and function declaration with no whitespaces at the end`() {
                val code = """
                    class C {

                        fun f() {
                            println("A message")
                            println("Another message") ;
                        }
                    }
                """.trimIndent()
                val rule = TrailingWhitespace()
                rule.visit(code.toKtFileContent())
                assertThat(rule.findings).isEmpty()
            }

            @Test
            fun `does not report an indentation inside multi-line strings`() {
                val code = """
                    val multiLineStringWithIndents = ""${'"'}
                        Should ignore indent on the next line
                        
                        Should ignore indent on the previous line
                    ""${'"'}
                """.trim()
                val rule = TrailingWhitespace()
                rule.visit(code.toKtFileContent())
                assertThat(rule.findings).isEmpty()
            }
        }
    }
}

private fun String.toKtFileContent(): KtFileContent {
    val file = compileContentForTest(this)
    val lines = file.text.splitToSequence("\n")
    return KtFileContent(file, lines)
}
