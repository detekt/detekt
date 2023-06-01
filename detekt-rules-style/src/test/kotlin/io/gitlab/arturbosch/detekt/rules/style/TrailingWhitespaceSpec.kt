package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class TrailingWhitespaceSpec {

    private val subject = TrailingWhitespace()

    @Nested
    inner class `positive cases` {
        @Test
        fun `reports a line just with a whitespace`() {
            val code = " "
            val findings = subject.compileAndLint(code)
            assertThat(findings).hasTextLocations(0 to 1)
        }

        @Test
        fun `reports a commented line with a whitespace at the end`() {
            val code = "// A comment "
            val findings = subject.compileAndLint(code)
            assertThat(findings).hasTextLocations(12 to 13)
        }

        @Test
        fun `reports a class declaration with a whitespace at the end`() {
            val code = "  class TrailingWhitespacePositive { \n  }"
            val findings = subject.compileAndLint(code)
            assertThat(findings).hasTextLocations(36 to 37)
        }

        @Test
        fun `reports a print statement with a tab at the end`() {
            val code = "\t\tprintln(\"A message\")\t"
            val findings = subject.compileAndLint(code)
            assertThat(findings).hasTextLocations(22 to 23)
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
            val findings = subject.compileAndLint(code)
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
            val findings = subject.compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }
}
