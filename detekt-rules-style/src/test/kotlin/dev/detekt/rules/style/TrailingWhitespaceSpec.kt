package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lint
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class TrailingWhitespaceSpec {

    private val subject = TrailingWhitespace(Config.Empty)

    @Nested
    inner class `positive cases` {
        @Test
        fun `reports a line just with a whitespace`() {
            val code = " "
            val findings = subject.lint(code)
            assertThat(findings).singleElement()
                .hasTextLocation(0 to 1)
        }

        @Test
        fun `reports a commented line with a whitespace at the end`() {
            val code = "// A comment "
            val findings = subject.lint(code)
            assertThat(findings).singleElement()
                .hasTextLocation(12 to 13)
        }

        @Test
        fun `reports a class declaration with a whitespace at the end`() {
            val code = "  class TrailingWhitespacePositive { \n  }"
            val findings = subject.lint(code)
            assertThat(findings).singleElement()
                .hasTextLocation(36 to 37)
        }

        @Test
        fun `reports a print statement with a tab at the end`() {
            val code = "fun test() {\n\t\tprintln(\"A message\")\t\n}"
            val findings = subject.lint(code)
            assertThat(findings).singleElement()
                .hasTextLocation(35 to 36)
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
            val findings = subject.lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report an indentation inside multi-line strings`() {
            val code = """
                val multiLineStringWithIndents = ""${'"'}
                    Should ignore indent on the next line
                    
                    Should ignore indent on the previous line
                ""${'"'}
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).isEmpty()
        }
    }
}
