package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class EmptyElseBlockSpec {

    private val subject = EmptyElseBlock(Config.empty)

    @Nested
    inner class `EmptyElseBlock rule` {
        @Test
        fun `reports empty else block`() {
            val code = """
                fun f() {
                    val i = 0
                    if (i == 0) {
                        println(i)
                    } else {
                        
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `reports empty else blocks with trailing semicolon`() {
            val code = """
                fun f() {
                    val i = 0
                    if (i == 0) {
                        println(i)
                    } else ;
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `reports empty else with trailing semicolon on new line`() {
            val code = """
                fun f() {
                    var i = 0
                    if (i == 0) {
                        println(i)
                    } else
                    ;
                    i++
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `reports empty else with trailing semicolon and braces`() {
            val code = """
                fun f() {
                    var i = 0
                    if (i == 0) {
                        println()
                    } else; {
                    }
                    i++
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `does not report nonempty else with braces`() {
            val code = """
                fun f() {
                    var i = 0
                    if (i == 0) {
                        println(i)
                    } else {
                        i++
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `does not report nonempty else without braces`() {
            val code = """
                fun f() {
                    var i = 0
                    if (i == 0) {
                        println(i)
                    } else i++
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `does not report nonempty else without braces but semicolon`() {
            val code = """
                fun f() {
                    var i = 0
                    if (i == 0) {
                        println(i)
                    } else i++;
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
}
